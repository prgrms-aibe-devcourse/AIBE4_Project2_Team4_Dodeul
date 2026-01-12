package org.aibe4.dodeul.domain.recommendation.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.aibe4.dodeul.domain.consulting.service.ConsultingApplicationService;
import org.aibe4.dodeul.domain.matching.service.MatchingService;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.entity.MemberConsultingTag;
import org.aibe4.dodeul.domain.member.service.MemberQueryService;
import org.aibe4.dodeul.domain.recommendation.model.dto.MentorRecommendationResponse;
import org.aibe4.dodeul.domain.review.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorRecommendationService {

    private final MemberQueryService memberQueryService;
    private final ConsultingApplicationService applicationService;
    private final MatchingService matchingService;
    private final ReviewService reviewService;

    private static final int RECOMMEND_NUM = 3;

    private static final double WEIGHT_CONSULTING = 0.4;
    private static final double WEIGHT_SKILL = 0.3;
    private static final double WEIGHT_REVIEW_RECOMMEND = 0.15;
    private static final double WEIGHT_MATCHING_COMPLETE = 0.1;
    private static final double WEIGHT_RESPONSE_RATE = 0.05;

    private static final double MIN_BENCHMARK_REVIEW = 5.0;
    private static final double MIN_BENCHMARK_MATCHING = 10.0;

    /**
     * 신청서의 상담 조건(유형, 스택)과 멘토의 활동 지표를 종합적으로 분석하고,
     * 동적 벤치마킹 기반의 적합도 점수가 높은 상위 멘토들을 추천
     */
    public List<MentorRecommendationResponse> recommendMentors(Long menteeId, Long applicationId) {
        ConsultingApplication application = applicationService.findApplicationEntity(applicationId);

        if (!application.getMenteeId().equals(menteeId)) {
            throw new IllegalArgumentException("본인의 신청서로만 매칭을 신청할 수 있습니다.");
        }

        ConsultingTag targetConsultingTag = application.getConsultingTag();
        List<String> targetSkillTags = application.getApplicationSkillTags().stream()
            .map(appTag -> appTag.getSkillTag().getName())
            .toList();

        // 후보군 필터링 (상담 가능 ON, 진행 중 매칭 < 3)
        List<Member> candidates = memberQueryService.findCandidateMentors();

        if (candidates.isEmpty() || candidates.size() < RECOMMEND_NUM) {
            throw new IllegalStateException("등록된 멘토 수가 적어 추천할 수 없습니다.");
        }

        // 후보가 되는 member의 id 리스트 추출
        // N+1 문제 방지를 위해 각 후보의 review, matching 데이터를 한 번에 가져옴
        List<Long> candidateIds = candidates.stream().map(Member::getId).toList();
        Map<Long, Long> reviewCounts = reviewService.getRecommendedReviewCounts(candidateIds);
        Map<Long, Long> matchingCounts = matchingService.getCompletedMatchingCounts(candidateIds);

        // review, matching는 후보군 내 최대값을 벤치마크로 산정
        // 최소 벤치마크 점수를 두어 과대 평가 보정
        long maxReviewVal = reviewCounts.values().stream().mapToLong(Long::longValue).max().orElse(0);
        long maxMatchingVal = matchingCounts.values().stream().mapToLong(Long::longValue).max().orElse(0);
        double benchmarkReview = Math.max(maxReviewVal, MIN_BENCHMARK_REVIEW);
        double benchmarkMatching = Math.max(maxMatchingVal, MIN_BENCHMARK_MATCHING);

        // 점수 산정
        return candidates.stream()
            .map(mentor -> {
                long rCount = reviewCounts.getOrDefault(mentor.getId(), 0L);
                long mCount = matchingCounts.getOrDefault(mentor.getId(), 0L);

                return calculateNormalizedScore(
                    mentor, targetConsultingTag, targetSkillTags,
                    rCount, mCount,
                    benchmarkReview, benchmarkMatching
                );
            })
            .sorted(Comparator.comparingDouble(MentorRecommendationResponse::getMatchScore).reversed())
            .limit(RECOMMEND_NUM)
            .collect(Collectors.toList());
    }

    /**
     * 상담 유형, 기술 스택 일치도 및 활동 지표(리뷰, 상담 수, 응답률)를 정규화하고 가중치를 적용하여,
     * 소수점 첫째 자리까지 표현된 100점 만점 기준의 매칭 적합도 점수를 산출
     */
    private MentorRecommendationResponse calculateNormalizedScore(
        Member mentor,
        ConsultingTag targetTag,
        List<String> targetSkills,
        long reviewCnt,
        long matchingCnt,
        double benchmarkReview,
        double benchmarkMatching) {

        boolean isConsultingMatched = mentor.getConsultingTags().stream()
            .map(MemberConsultingTag::getConsultingTag)
            .anyMatch(tag -> tag == targetTag);
        double normConsulting = isConsultingMatched ? 1.0 : 0.0;

        double normSkill = 1.0;
        if (targetSkills != null && !targetSkills.isEmpty()) {
            List<String> mentorSkills = mentor.getSkillTags().stream()
                .map(mst -> mst.getSkillTag().getName())
                .toList();

            long matchCount = targetSkills.stream()
                .filter(mentorSkills::contains)
                .count();

            normSkill = (double) matchCount / targetSkills.size();
        }

        double normReview = Math.min(reviewCnt / benchmarkReview, 1.0);
        double normMatching = Math.min(matchingCnt / benchmarkMatching, 1.0);
        double normResponse = mentor.getMentorProfile().getResponseRate() / 100.0;

        double weightedSum = (normConsulting * WEIGHT_CONSULTING)
            + (normSkill * WEIGHT_SKILL)
            + (normReview * WEIGHT_REVIEW_RECOMMEND)
            + (normMatching * WEIGHT_MATCHING_COMPLETE)
            + (normResponse * WEIGHT_RESPONSE_RATE);
        double totalScore = Math.floor(weightedSum * 100 * 10) / 10.0;

        return MentorRecommendationResponse.builder()
            .mentorId(mentor.getId())
            .nickname(mentor.getNickname())
            .profileUrl(mentor.getMentorProfile().getProfileUrl())
            .job(mentor.getMentorProfile().getJob())
            .careerYears(mentor.getMentorProfile().getCareerYears())
            .skillTags(mentor.getSkillTags().stream()
                .map(mst -> mst.getSkillTag().getName())
                .collect(Collectors.toList()))
            .recommendedReviewCount(reviewCnt)
            .completedMatchingCount(matchingCnt)
            .responseRate(normResponse)
            .matchScore(totalScore)
            .build();
    }
}

package org.aibe4.dodeul.domain.matching.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;
import org.aibe4.dodeul.domain.consulting.service.ConsultingApplicationService;
import org.aibe4.dodeul.domain.matching.model.dto.MatchingCreateRequest;
import org.aibe4.dodeul.domain.matching.model.dto.MatchingStatusResponse;
import org.aibe4.dodeul.domain.matching.model.entity.Matching;
import org.aibe4.dodeul.domain.matching.model.enums.MatchingStatus;
import org.aibe4.dodeul.domain.matching.model.repository.MatchingRepository;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.aibe4.dodeul.domain.member.service.MemberQueryService;
import org.aibe4.dodeul.domain.member.service.MemberService;
import org.aibe4.dodeul.global.exception.BusinessException;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingService {

    private static final int MAX_ACTIVE_MATCHING_COUNT = 3;
    private static final List<MatchingStatus> ACTIVE_STATUSES = List.of(MatchingStatus.WAITING, MatchingStatus.MATCHED);
    private static final List<MatchingStatus> RESPONDED_STATUSES = List.of(
        MatchingStatus.MATCHED,
        MatchingStatus.REJECTED,
        MatchingStatus.INREVIEW,
        MatchingStatus.COMPLETED
    );
    private static final List<MatchingStatus> IGNORED_STATUSES = List.of(
        MatchingStatus.TIMEOUT
    );

    private final MatchingRepository matchingRepository;

    private final MemberService memberService;
    private final MemberQueryService memberQueryService;
    private final ConsultingApplicationService applicationService;

    public void validateMenteeMatchingAvailability(Long menteeId) {
        long menteeActiveCount = matchingRepository.countByMenteeIdAndStatusIn(menteeId, ACTIVE_STATUSES);
        if (menteeActiveCount >= MAX_ACTIVE_MATCHING_COUNT) {
            throw new BusinessException(ErrorCode.MENTEE_MATCHING_LIMIT_EXCEEDED, "동시에 진행 가능한 상담 수는 최대 3개입니다. 기존의 상담을 먼저 끝내주세요.");
        }
    }

    public void validateMentorMatchingAvailability(Long mentorId) {
        memberQueryService.validateMentorConsultationEnabled(mentorId);

        long mentorActiveCount = matchingRepository.countByMentorIdAndStatusIn(mentorId, ACTIVE_STATUSES);
        if (mentorActiveCount >= MAX_ACTIVE_MATCHING_COUNT) {
            throw new BusinessException(ErrorCode.MENTOR_MATCHING_LIMIT_EXCEEDED, "해당 멘토의 상담이 마감되었습니다. 다른 멘토를 선택해주세요.");
        }
    }

    public Map<Long, Long> getCompletedMatchingCounts(List<Long> mentorIds) {
        if (mentorIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return matchingRepository.countCompletedMatchingsByMentorIds(mentorIds).stream()
            .collect(Collectors.toMap(
                obj -> (Long) obj[0],
                obj -> (Long) obj[1]
            ));
    }

    @Transactional
    public void updateMentorResponseRate(Long mentorId) {
        long respondedCount = matchingRepository.countByMentorIdAndStatusIn(mentorId, RESPONDED_STATUSES);
        long ignoredCount = matchingRepository.countByMentorIdAndStatusIn(mentorId, IGNORED_STATUSES);

        double rawRate;
        if (respondedCount + ignoredCount == 0) {
            rawRate = 0.0;
        } else {
            rawRate = (double) respondedCount / (respondedCount + ignoredCount);
        }

        double responseRate = Math.round(rawRate * 100 * 10) / 10.0;
        memberQueryService.updateMentorResponseRate(mentorId, responseRate);
    }

    @Transactional
    @PreAuthorize("hasRole('MENTEE')")
    public MatchingStatusResponse createMatching(Long menteeId, MatchingCreateRequest request) {

        ConsultingApplication application = applicationService.findApplicationEntity(request.getApplicationId());

        if (!application.getMenteeId().equals(menteeId)) {
            throw new IllegalArgumentException("본인의 신청서로만 매칭을 신청할 수 있습니다.");
        }

        Member mentee = memberService.getMemberOrThrow(menteeId);
        Member mentor = memberService.getMemberOrThrow(request.getMentorId());

        if (mentee.getRole() != Role.MENTEE || mentor.getRole() != Role.MENTOR) {
            throw new IllegalArgumentException("멘토와 멘티의 역할이 올바르지 않습니다.");
        }

        validateMenteeMatchingAvailability(menteeId);
        validateMentorMatchingAvailability(request.getMentorId());

        Matching matching = Matching.builder()
            .mentee(mentee)
            .mentor(mentor)
            .application(application)
            .build();
        matchingRepository.save(matching);

        return new MatchingStatusResponse(matching.getId(), matching.getStatus());
    }

    public Matching findMatchingEntity(Long matchingId) {
        return matchingRepository.findById(matchingId)
            .orElseThrow(() -> new NoSuchElementException("해당 매칭 정보를 찾을 수 없습니다."));
    }

    @Transactional
    public MatchingStatusResponse acceptMatching(Long matchingId, Long memberId) {
        Matching matching = findMatchingEntity(matchingId);

        if (!matching.getMentor().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 매칭만 수락할 수 있습니다.");
        }
        matching.accept();
        updateMentorResponseRate(memberId);

        return new MatchingStatusResponse(matchingId, matching.getStatus());
    }

    @Transactional
    public MatchingStatusResponse rejectMatching(Long matchingId, Long memberId) {
        Matching matching = findMatchingEntity(matchingId);

        if (!matching.getMentor().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 매칭만 거절할 수 있습니다.");
        }
        matching.reject();
        updateMentorResponseRate(memberId);

        return new MatchingStatusResponse(matchingId, matching.getStatus());
    }

    @Transactional
    public MatchingStatusResponse cancelMatching(Long matchingId, Long memberId) {
        Matching matching = findMatchingEntity(matchingId);

        if (!matching.getMentee().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인이 신청한 매칭만 취소할 수 있습니다.");
        }
        matching.cancel();

        return new MatchingStatusResponse(matchingId, matching.getStatus());
    }

    @Transactional
    public MatchingStatusResponse finishConsulting(Long matchingId, Long memberId) {
        Matching matching = findMatchingEntity(matchingId);

        if (!matching.getMentor().getId().equals(memberId) && !matching.getMentee().getId().equals(memberId)) {
            throw new IllegalArgumentException("상담 참여자만 종료할 수 있습니다.");
        }
        matching.finishConsulting();

        return new MatchingStatusResponse(matchingId, matching.getStatus());
    }

    @Transactional
    public MatchingStatusResponse completeMatching(Long matchingId, Long memberId) {
        Matching matching = findMatchingEntity(matchingId);

        if (!matching.getMentee().getId().equals(memberId)) {
            throw new IllegalArgumentException("멘티만 최종 완료를 할 수 있습니다.");
        }
        matching.complete();

        return new MatchingStatusResponse(matchingId, matching.getStatus());
    }
}

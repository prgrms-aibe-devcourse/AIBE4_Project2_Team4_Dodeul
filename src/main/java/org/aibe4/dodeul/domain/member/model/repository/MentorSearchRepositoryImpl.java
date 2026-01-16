package org.aibe4.dodeul.domain.member.model.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.aibe4.dodeul.domain.matching.MatchingConstants;
import org.aibe4.dodeul.domain.matching.model.enums.MatchingStatus;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.aibe4.dodeul.domain.search.model.dto.MentorSearchCondition;
import org.aibe4.dodeul.domain.search.model.dto.MentorSearchResponse;
import org.aibe4.dodeul.domain.search.model.enums.SortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.aibe4.dodeul.domain.matching.model.entity.QMatching.matching;
import static org.aibe4.dodeul.domain.member.model.entity.QMember.member;
import static org.aibe4.dodeul.domain.member.model.entity.QMentorProfile.mentorProfile;

@RequiredArgsConstructor
public class MentorSearchRepositoryImpl implements MentorSearchRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MentorSearchResponse> searchMentors(MentorSearchCondition condition, Pageable pageable) {

        // 현재 진행 중인(WAITING, MATCHED) 매칭 개수 조회 서브쿼리
        // "상담 가능한 멘토만 보기" 필터링용
        JPQLQuery<Long> activeMatchingCountSub = JPAExpressions
            .select(matching.count())
            .from(matching)
            .where(matching.mentor.eq(member)
                .and(matching.status.in(MatchingStatus.WAITING, MatchingStatus.MATCHED)));

        // 멘토 목록 조회 쿼리
        List<Member> members = queryFactory
            .selectFrom(member)
            .join(member.mentorProfile, mentorProfile).fetchJoin()
            .where(
                member.role.eq(Role.MENTOR),
                nicknameContains(condition.getKeyword()),
                jobIn(condition.getJobs()),
                skillTagIn(condition.getSkillTags()),
                consultingTagIn(condition.getConsultingTags()),
                onlyAvailable(condition.getOnlyAvailable(), activeMatchingCountSub)
            )
            .orderBy(getOrderSpecifier(condition.getSortType()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .distinct()
            .fetch();

        // 조회된 멤버들의 ID만 추출
        List<Long> memberIds = members.stream().map(Member::getId).toList();

        // 조회된 멘토들의 진행 중 매칭 개수를 한 방에 가져옴
        List<Tuple> countResults = queryFactory
            .select(matching.mentor.id, matching.count()) // select 절 명시
            .from(matching)
            .where(
                matching.mentor.id.in(memberIds),
                matching.status.in(MatchingStatus.WAITING, MatchingStatus.MATCHED)
            )
            .groupBy(matching.mentor.id)
            .fetch();
        Map<Long, Long> matchingCounts = countResults.stream()
            .collect(Collectors.toMap(
                tuple -> tuple.get(matching.mentor.id), // Key: 멘토 ID
                tuple -> tuple.get(matching.count())    // Value: 카운트
            ));

        // 엔티티를 DTO로 변환
        List<MentorSearchResponse> content = members.stream()
            .map(m -> {
                // 맵에 값이 없으면 0L (진행 중인 매칭 없음)
                Long count = matchingCounts.getOrDefault(m.getId(), 0L);
                return MentorSearchResponse.from(m, count);
            })
            .collect(Collectors.toList());

        // 조회된 전체 멤버 수 계산 쿼리 정의
        JPAQuery<Long> countQuery = queryFactory
            .select(member.countDistinct())
            .from(member)
            .join(member.mentorProfile, mentorProfile)
            .where(
                member.role.eq(Role.MENTOR),
                nicknameContains(condition.getKeyword()),
                jobIn(condition.getJobs()),
                skillTagIn(condition.getSkillTags()),
                consultingTagIn(condition.getConsultingTags()),
                onlyAvailable(condition.getOnlyAvailable(), activeMatchingCountSub)
            );

        // PageableExecutionUtils를 사용하여 필요할 때만 Count 쿼리 실행
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<MentorSearchResponse> findPopularMentors() {

        // 추천 수(recommendCount) 내림차순으로 상위 10명 멘토 조회
        List<Member> members = queryFactory
            .selectFrom(member)
            .join(member.mentorProfile, mentorProfile).fetchJoin()
            .where(member.role.eq(Role.MENTOR))
            .orderBy(
                mentorProfile.recommendCount.desc(),
                member.id.desc()
            )
            .limit(10)
            .fetch();

        if (members.isEmpty()) {
            return List.of();
        }

        List<Long> memberIds = members.stream()
            .map(Member::getId)
            .toList();

        List<Tuple> countResults = queryFactory
            .select(matching.mentor.id, matching.count())
            .from(matching)
            .where(
                matching.mentor.id.in(memberIds),
                matching.status.in(MatchingStatus.WAITING, MatchingStatus.MATCHED)
            )
            .groupBy(matching.mentor.id)
            .fetch();

        Map<Long, Long> matchingCounts = countResults.stream()
            .collect(Collectors.toMap(
                tuple -> tuple.get(matching.mentor.id),
                tuple -> tuple.get(matching.count())
            ));

        return members.stream()
            .map(m -> {
                Long count = matchingCounts.getOrDefault(m.getId(), 0L);
                return MentorSearchResponse.from(m, count);
            })
            .collect(Collectors.toList());
    }

    private BooleanExpression nicknameContains(String keyword) {
        return StringUtils.hasText(keyword) ? member.nickname.contains(keyword) : null;
    }

    private BooleanExpression jobIn(List<String> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            return null;
        }
        return mentorProfile.job.in(jobs);
    }

    private BooleanExpression skillTagIn(List<String> skillTags) {
        if (skillTags == null || skillTags.isEmpty()) {
            return null;
        }
        return member.skillTags.any().skillTag.name.in(skillTags);
    }

    private BooleanExpression consultingTagIn(List<ConsultingTag> consultingTags) {
        if (consultingTags == null || consultingTags.isEmpty()) {
            return null;
        }
        return member.consultingTags.any().consultingTag.in(consultingTags);
    }

    private BooleanExpression onlyAvailable(Boolean onlyAvailable, JPQLQuery<Long> activeMatchingCountSub) {
        if (Boolean.TRUE.equals(onlyAvailable)) {
            return mentorProfile.consultationEnabled.isTrue()
                .and(activeMatchingCountSub.lt((long) MatchingConstants.MAX_ACTIVE_MATCHING_COUNT));
        }
        return null;
    }

    private OrderSpecifier<?> getOrderSpecifier(SortType sortType) {
        if (sortType == null) {
            return new OrderSpecifier<>(Order.DESC, member.createdAt);
        }

        switch (sortType) {
            case RECOMMEND:
                return mentorProfile.recommendCount.desc();

            case MATCHING:
                return mentorProfile.completedMatchingCount.desc();

            case RESPONSE:
                return mentorProfile.responseRate.desc();

            case LATEST:
            default:
                return new OrderSpecifier<>(Order.DESC, member.createdAt);
        }
    }
}

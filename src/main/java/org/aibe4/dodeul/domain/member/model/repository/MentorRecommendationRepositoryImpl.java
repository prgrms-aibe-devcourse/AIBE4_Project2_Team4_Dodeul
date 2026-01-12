package org.aibe4.dodeul.domain.member.model.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.matching.model.enums.MatchingStatus;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.aibe4.dodeul.domain.matching.model.entity.QMatching.matching;
import static org.aibe4.dodeul.domain.member.model.entity.QMember.member;
import static org.aibe4.dodeul.domain.member.model.entity.QMentorProfile.mentorProfile;

@Repository
@RequiredArgsConstructor
public class MentorRecommendationRepositoryImpl implements MentorRecommendationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findCandidateMentors() {
        return queryFactory
            .selectFrom(member)
            .join(member.mentorProfile, mentorProfile).fetchJoin()
            .where(
                member.role.eq(Role.MENTOR),
                mentorProfile.consultationEnabled.isTrue(),
                JPAExpressions.select(matching.count())
                    .from(matching)
                    .where(
                        matching.mentor.eq(member),
                        matching.status.in(MatchingStatus.WAITING, MatchingStatus.MATCHED)
                    ).lt(3L)
            )
            .fetch();
    }
}

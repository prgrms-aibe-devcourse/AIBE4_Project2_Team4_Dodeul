package org.aibe4.dodeul.domain.member.model.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.aibe4.dodeul.domain.matching.model.enums.MatchingStatus;
import org.aibe4.dodeul.domain.member.model.dto.MentorCandidateDto;
import org.aibe4.dodeul.domain.member.model.dto.QMentorCandidateDto;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.aibe4.dodeul.domain.common.model.entity.QSkillTag.skillTag;
import static org.aibe4.dodeul.domain.matching.model.entity.QMatching.matching;
import static org.aibe4.dodeul.domain.member.model.entity.QMember.member;
import static org.aibe4.dodeul.domain.member.model.entity.QMemberConsultingTag.memberConsultingTag;
import static org.aibe4.dodeul.domain.member.model.entity.QMemberSkillTag.memberSkillTag;
import static org.aibe4.dodeul.domain.member.model.entity.QMentorProfile.mentorProfile;

@Repository
@RequiredArgsConstructor
public class MentorRecommendationRepositoryImpl implements MentorRecommendationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * DTO Projection을 사용한 멘토 후보군 조회
     * 엔티티 조회(N+1 위험) 대신 필요한 필드만 가져와 성능 최적화
     */
    @Override
    public List<MentorCandidateDto> findCandidateMentorsDto() {
        List<MentorCandidateDto> mentors = queryFactory
            .select(new QMentorCandidateDto(
                member.id,
                member.nickname,
                mentorProfile.job,
                mentorProfile.careerYears,
                mentorProfile.profileUrl,
                mentorProfile.responseRate,
                mentorProfile.recommendCount,
                mentorProfile.completedMatchingCount
            ))
            .from(member)
            .join(member.mentorProfile, mentorProfile)
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

        if (mentors.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> mentorIds = mentors.stream().map(MentorCandidateDto::getId).toList();

        List<Tuple> skillTuples = queryFactory
            .select(memberSkillTag.member.id, skillTag.name)
            .from(memberSkillTag)
            .join(memberSkillTag.skillTag, skillTag)
            .where(memberSkillTag.member.id.in(mentorIds))
            .fetch();

        Map<Long, List<String>> skillMap = skillTuples.stream()
            .collect(Collectors.groupingBy(
                tuple -> tuple.get(memberSkillTag.member.id),
                Collectors.mapping(tuple -> tuple.get(skillTag.name), Collectors.toList())
            ));

        List<Tuple> consultingTuples = queryFactory
            .select(memberConsultingTag.member.id, memberConsultingTag.consultingTag)
            .from(memberConsultingTag)
            .where(memberConsultingTag.member.id.in(mentorIds))
            .fetch();

        Map<Long, List<ConsultingTag>> consultingMap = consultingTuples.stream()
            .collect(Collectors.groupingBy(
                tuple -> tuple.get(memberConsultingTag.member.id),
                Collectors.mapping(tuple -> tuple.get(memberConsultingTag.consultingTag), Collectors.toList())
            ));

        mentors.forEach(dto -> {
            dto.setSkillTags(skillMap.getOrDefault(dto.getId(), Collections.emptyList()));
            dto.setConsultingTags(consultingMap.getOrDefault(dto.getId(), Collections.emptyList()));
        });

        return mentors;
    }
}

package org.aibe4.dodeul.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;
import org.aibe4.dodeul.domain.member.model.dto.response.MentorPublicProfileResponse;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.entity.MemberConsultingTag;
import org.aibe4.dodeul.domain.member.model.entity.MemberSkillTag;
import org.aibe4.dodeul.domain.member.model.entity.MentorProfile;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.aibe4.dodeul.domain.member.model.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorPublicProfileService {

    private final MemberRepository memberRepository;

    public MentorPublicProfileResponse getMentorPublicProfile(Long mentorId) {
        Member mentor = memberRepository.findMentorPublicProfileById(mentorId)
            .orElseThrow(() -> new IllegalArgumentException("대상을 찾을 수 없습니다."));

        if (mentor.getRole() != Role.MENTOR) {
            throw new IllegalArgumentException("멘토가 아닙니다.");
        }

        MentorProfile mentorProfile = mentor.getMentorProfile();

        String profileUrl = (mentorProfile != null) ? mentorProfile.getProfileUrl() : null;

        // 기술스택 태그 이름 목록
        List<String> skillTags = mentor.getSkillTags().stream()
            .map(MemberSkillTag::getSkillTag)
            .map(SkillTag::getName)
            .distinct()
            .toList();

        // 상담 가능 분야 (ConsultingTag enum name)
        List<String> consultingFields = mentor.getConsultingTags().stream()
            .map(MemberConsultingTag::getConsultingTag)
            .map(Enum::name)
            .distinct()
            .toList();

        return MentorPublicProfileResponse.builder()
            .mentorId(mentor.getId())
            .nickname(mentor.getNickname())
            .profileUrl(profileUrl)

            .job(mentorProfile != null ? mentorProfile.getJob() : null)
            .intro(mentorProfile != null ? mentorProfile.getIntro() : null)
            .careerYears(mentorProfile != null ? mentorProfile.getCareerYears() : null)
            .consultationEnabled(mentorProfile != null ? mentorProfile.isConsultationEnabled() : null)

            .skillTags(skillTags)
            .consultingFields(consultingFields)
            .build();
    }
}

package org.aibe4.dodeul.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;
import org.aibe4.dodeul.domain.common.repository.SkillTagRepository;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.aibe4.dodeul.domain.member.model.dto.request.MenteeProfileUpdateRequest;
import org.aibe4.dodeul.domain.member.model.dto.request.MentorProfileUpdateRequest;
import org.aibe4.dodeul.domain.member.model.entity.*;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.aibe4.dodeul.domain.member.model.repository.*;
import org.aibe4.dodeul.global.exception.BusinessException;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MyProfileService {

    private final MemberRepository memberRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final MenteeProfileRepository menteeProfileRepository;

    private final SkillTagRepository skillTagRepository;
    private final MemberSkillTagRepository memberSkillTagRepository;
    private final MemberConsultingTagRepository memberConsultingTagRepository;

    public void updateMentorProfile(Long memberId, MentorProfileUpdateRequest req) {
        Member member = getMemberOrThrow(memberId);
        if (member.getRole() != Role.MENTOR) throw new BusinessException(ErrorCode.ACCESS_DENIED);

        MentorProfile profile = mentorProfileRepository.findById(memberId)
            .orElseGet(() -> mentorProfileRepository.save(MentorProfile.create(member)));

        profile.updateProfile(
            req.getProfileUrl(),
            req.getIntro(),
            req.getJob(),
            req.getCareerYears(),
            req.getConsultationEnabled() != null && req.getConsultationEnabled()
        );
        
        replaceSkillTags(member, req.getSkillTags());
        replaceConsultingTags(member, req.getConsultingTags());
    }

    public void updateMenteeProfile(Long memberId, MenteeProfileUpdateRequest req) {
        Member member = getMemberOrThrow(memberId);
        if (member.getRole() != Role.MENTEE) throw new BusinessException(ErrorCode.ACCESS_DENIED);

        MenteeProfile profile = menteeProfileRepository.findById(memberId)
            .orElseGet(() -> menteeProfileRepository.save(MenteeProfile.create(member)));

        profile.updateProfile(req.getProfileUrl(), req.getIntro(), req.getJob());

        replaceSkillTags(member, req.getSkillTags());
        replaceConsultingTags(member, req.getConsultingTags());
    }

    private void replaceSkillTags(Member member, List<String> names) {
        // 기존 전부 삭제 (중복/정합성 제일 깔끔)
        memberSkillTagRepository.deleteAllByMemberId(member.getId());

        if (names == null) return;

        for (String raw : names) {
            if (raw == null) continue;
            String name = raw.trim();
            if (name.isBlank()) continue;

            SkillTag skillTag = skillTagRepository.findByName(name)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 스킬 태그입니다: " + name));

            memberSkillTagRepository.save(new MemberSkillTag(member, skillTag));
        }
    }

    private void replaceConsultingTags(Member member, List<String> names) {
        memberConsultingTagRepository.deleteAllByMemberId(member.getId());

        if (names == null) return;

        for (String raw : names) {
            if (raw == null) continue;
            String name = raw.trim();
            if (name.isBlank()) continue;

            ConsultingTag tag;
            try {
                tag = ConsultingTag.valueOf(name);
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "상담 태그 형식이 올바르지 않습니다: " + name);
            }

            memberConsultingTagRepository.save(new MemberConsultingTag(member, tag));
        }
    }

    private Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS, "인증 정보가 유효하지 않습니다."));
    }
}

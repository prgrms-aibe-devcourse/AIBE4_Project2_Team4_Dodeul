package org.aibe4.dodeul.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.entity.MentorProfile;
import org.aibe4.dodeul.domain.member.model.repository.MentorProfileRepository;
import org.aibe4.dodeul.global.exception.BusinessException;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MentorProfileRepository mentorProfileRepository;

    public void validateMentorConsultationEnabled(Long mentorId) {
        MentorProfile mentorProfile = mentorProfileRepository.findById(mentorId)
            .orElseThrow(() -> new BusinessException(ErrorCode.MENTOR_NOT_FOUND));

        if (!mentorProfile.isConsultationEnabled()) {
            throw new BusinessException(ErrorCode.MENTOR_CONSULTATION_DISABLED);
        }
    }
}

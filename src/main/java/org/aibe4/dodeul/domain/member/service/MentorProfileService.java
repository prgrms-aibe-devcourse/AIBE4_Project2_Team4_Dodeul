package org.aibe4.dodeul.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.entity.MentorProfile;
import org.aibe4.dodeul.domain.member.model.repository.MentorProfileRepository;
import org.aibe4.dodeul.global.exception.BusinessException;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MentorProfileService {

    private final MentorProfileRepository mentorProfileRepository;

    /**
     * 멘토가 상담 신청을 받을지 여부를 토글합니다.
     * true: 상담 가능, false: 상담 불가
     */
    @Transactional
    public void updateConsultationEnabled(CustomUserDetails userDetails, boolean enabled) {
        if (userDetails == null) {
            // 프로젝트에서 실제로 쓰는 코드(SecurityConfig) 기준으로 맞춤
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        Long memberId = userDetails.getMemberId();

        MentorProfile profile = mentorProfileRepository.findById(memberId)
            .orElseThrow(() ->
                new IllegalStateException("멘토 프로필이 존재하지 않습니다. memberId=" + memberId)
            );

        profile.changeConsultationEnabled(enabled);
    }
}

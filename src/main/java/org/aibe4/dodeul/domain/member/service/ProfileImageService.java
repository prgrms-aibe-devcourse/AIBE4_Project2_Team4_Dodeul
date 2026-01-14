package org.aibe4.dodeul.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.entity.MenteeProfile;
import org.aibe4.dodeul.domain.member.model.entity.MentorProfile;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.aibe4.dodeul.domain.member.model.repository.MemberRepository;
import org.aibe4.dodeul.domain.member.model.repository.MenteeProfileRepository;
import org.aibe4.dodeul.domain.member.model.repository.MentorProfileRepository;
import org.aibe4.dodeul.global.exception.BusinessException;
import org.aibe4.dodeul.global.file.model.dto.response.FileUploadResponse;
import org.aibe4.dodeul.global.file.service.FileService;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileImageService {

    private final FileService fileService;
    private final MemberRepository memberRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final MenteeProfileRepository menteeProfileRepository;

    public String uploadMentorProfileImage(Long memberId, MultipartFile file) {
        Member member = getMemberOrThrow(memberId);
        if (member.getRole() != Role.MENTOR) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        MentorProfile profile = mentorProfileRepository.findById(memberId)
            .orElseGet(() -> mentorProfileRepository.save(MentorProfile.create(member)));

        FileUploadResponse upload =
            fileService.upload(file, "profile/mentor");

        profile.updateProfile(
            upload.getFileUrl(),
            profile.getIntro(),
            profile.getJob(),
            profile.getCareerYears(),
            profile.isConsultationEnabled()
        );

        return upload.getFileUrl();
    }

    public String uploadMenteeProfileImage(Long memberId, MultipartFile file) {
        Member member = getMemberOrThrow(memberId);
        if (member.getRole() != Role.MENTEE) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        MenteeProfile profile = menteeProfileRepository.findById(memberId)
            .orElseGet(() -> menteeProfileRepository.save(MenteeProfile.create(member)));

        FileUploadResponse upload =
            fileService.upload(file, "profile/mentee");

        profile.updateProfile(
            upload.getFileUrl(),
            profile.getIntro(),
            profile.getJob()
        );

        return upload.getFileUrl();
    }

    private Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessException(
                ErrorCode.UNAUTHORIZED_ACCESS,
                "인증 정보가 유효하지 않습니다."
            ));
    }
}

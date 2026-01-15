package org.aibe4.dodeul.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.service.ProfileImageService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class ProfileImageApiController {

    private final ProfileImageService profileImageService;

    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping(value = "/mentor/profile-image", consumes = "multipart/form-data")
    public CommonResponse<String> uploadMentorProfileImage(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestPart("file") MultipartFile file
    ) {
        String url = profileImageService.uploadMentorProfileImage(user.getMemberId(), file);
        return CommonResponse.success(SuccessCode.UPDATE_SUCCESS, url, "멘토 프로필 이미지 업로드 성공");
    }

    @PreAuthorize("hasRole('MENTEE')")
    @PostMapping(value = "/mentee/profile-image", consumes = "multipart/form-data")
    public CommonResponse<String> uploadMenteeProfileImage(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestPart("file") MultipartFile file
    ) {
        String url = profileImageService.uploadMenteeProfileImage(user.getMemberId(), file);
        return CommonResponse.success(SuccessCode.UPDATE_SUCCESS, url, "멘티 프로필 이미지 업로드 성공");
    }
}

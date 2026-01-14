package org.aibe4.dodeul.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.dto.request.MenteeProfileUpdateRequest;
import org.aibe4.dodeul.domain.member.model.dto.request.MentorProfileUpdateRequest;
import org.aibe4.dodeul.domain.member.service.MyProfileService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyProfileApiController {

    private final MyProfileService myProfileService;

    @PreAuthorize("hasRole('MENTOR')")
    @PatchMapping("/mentor/profile")
    public CommonResponse<Void> updateMentor(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestBody MentorProfileUpdateRequest req
    ) {
        myProfileService.updateMentorProfile(user.getMemberId(), req);
        return CommonResponse.success(SuccessCode.UPDATE_SUCCESS, null, "멘토 프로필 수정 성공");
    }

    @PreAuthorize("hasRole('MENTEE')")
    @PatchMapping("/mentee/profile")
    public CommonResponse<Void> updateMentee(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestBody MenteeProfileUpdateRequest req
    ) {
        myProfileService.updateMenteeProfile(user.getMemberId(), req);
        return CommonResponse.success(SuccessCode.UPDATE_SUCCESS, null, "멘티 프로필 수정 성공");
    }
}

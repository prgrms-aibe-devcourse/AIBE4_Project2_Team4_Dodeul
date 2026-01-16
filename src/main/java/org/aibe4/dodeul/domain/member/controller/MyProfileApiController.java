package org.aibe4.dodeul.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.dto.request.MenteeProfileUpdateRequest;
import org.aibe4.dodeul.domain.member.model.dto.request.MentorProfileUpdateRequest;
import org.aibe4.dodeul.domain.member.model.dto.response.MenteeMyProfileResponse;
import org.aibe4.dodeul.domain.member.model.dto.response.MentorMyProfileResponse;
import org.aibe4.dodeul.domain.member.service.MyProfileService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member", description = "회원/마이페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyProfileApiController {

    private final MyProfileService myProfileService;

    // 멘토 내 프로필 조회
    @Operation(summary = "멘토 내 프로필 조회")
    @MemberSwaggerDocs.GetProfile
    @PreAuthorize("hasRole('MENTOR')")
    @GetMapping("/mentor/profile")
    public CommonResponse<MentorMyProfileResponse> getMentorProfile(
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        MentorMyProfileResponse data = myProfileService.getMentorMyProfile(user.getMemberId());
        return CommonResponse.success(SuccessCode.SUCCESS, data, "멘토 프로필 조회 성공");
    }

    // 멘토 프로필 수정
    @Operation(summary = "멘토 프로필 수정")
    @MemberSwaggerDocs.UpdateProfile
    @PreAuthorize("hasRole('MENTOR')")
    @PatchMapping("/mentor/profile")
    public CommonResponse<Void> updateMentor(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestBody MentorProfileUpdateRequest req
    ) {
        myProfileService.updateMentorProfile(user.getMemberId(), req);
        return CommonResponse.success(SuccessCode.UPDATE_SUCCESS, null, "멘토 프로필 수정 성공");
    }

    // 멘티 내 프로필 조회
    @Operation(summary = "멘티 내 프로필 조회")
    @MemberSwaggerDocs.GetProfile
    @PreAuthorize("hasRole('MENTEE')")
    @GetMapping("/mentee/profile")
    public CommonResponse<MenteeMyProfileResponse> getMenteeProfile(
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        MenteeMyProfileResponse data = myProfileService.getMenteeMyProfile(user.getMemberId());
        return CommonResponse.success(SuccessCode.SUCCESS, data, "멘티 프로필 조회 성공");
    }

    // 멘티 프로필 수정
    @Operation(summary = "멘티 프로필 수정")
    @MemberSwaggerDocs.UpdateProfile
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

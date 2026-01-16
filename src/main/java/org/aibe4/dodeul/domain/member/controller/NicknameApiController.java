package org.aibe4.dodeul.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.dto.request.NicknameUpdateRequest;
import org.aibe4.dodeul.domain.member.service.MemberService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원/마이페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class NicknameApiController {

    private final MemberService memberService;

    @Operation(summary = "닉네임 설정", description = "회원 닉네임을 설정/변경합니다.")
    @MemberSwaggerDocs.UpdateNickname
    @PutMapping("/nickname")
    public CommonResponse<Void> updateNickname(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestBody NicknameUpdateRequest request
    ) {
        memberService.updateNickname(user.getMemberId(), request.nickname());
        return CommonResponse.success(SuccessCode.UPDATE_SUCCESS, null);
    }
}

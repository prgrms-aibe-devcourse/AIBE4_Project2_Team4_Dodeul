package org.aibe4.dodeul.domain.member.controller;

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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class NicknameApiController {

    private final MemberService memberService;

    @PutMapping("/nickname")
    public CommonResponse<Void> updateNickname(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestBody NicknameUpdateRequest request
    ) {
        memberService.updateNickname(user.getMemberId(), request.nickname());
        return CommonResponse.success(SuccessCode.UPDATE_SUCCESS, null);
    }
}

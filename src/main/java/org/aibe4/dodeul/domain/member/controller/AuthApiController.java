package org.aibe4.dodeul.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.dto.RegisterRequest;
import org.aibe4.dodeul.domain.member.service.MemberService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final MemberService memberService;

    @PostMapping("/register")
    public CommonResponse<Long> register(@RequestBody RegisterRequest request) {
        Long memberId =
                memberService.registerLocal(request.email(), request.password(), request.role());

        return CommonResponse.success(SuccessCode.SIGNUP_SUCCESS, memberId);
    }
}

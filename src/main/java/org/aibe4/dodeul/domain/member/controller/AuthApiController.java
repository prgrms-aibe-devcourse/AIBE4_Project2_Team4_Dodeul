package org.aibe4.dodeul.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.dto.request.RegisterRequest;
import org.aibe4.dodeul.domain.member.service.MemberService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원/마이페이지 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final MemberService memberService;

    @Operation(summary = "회원가입", description = "이메일/비밀번호 기반 회원가입 (역할 선택 포함)")
    @MemberSwaggerDocs.Register
    @PostMapping("/register")
    public CommonResponse<Long> register(@RequestBody RegisterRequest request) {
        Long memberId =
            memberService.registerLocal(
                request.email(),
                request.password(),
                request.confirmPassword(),
                request.role()
            );

        return CommonResponse.success(SuccessCode.SIGNUP_SUCCESS, memberId);
    }
}

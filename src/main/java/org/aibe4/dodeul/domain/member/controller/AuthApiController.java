package org.aibe4.dodeul.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.dto.RegisterRequest;
import org.aibe4.dodeul.domain.member.service.MemberService;
import org.aibe4.dodeul.global.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<Long>> register(@RequestBody RegisterRequest request) {
        Long memberId =
                memberService.registerLocal(request.email(), request.password(), request.role());
        return ResponseEntity.status(201).body(ApiResponse.created("회원가입 성공", memberId));
    }
}

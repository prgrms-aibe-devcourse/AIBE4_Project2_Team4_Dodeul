package org.aibe4.dodeul.domain.member.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.dto.AuthSessionKeys;
import org.aibe4.dodeul.domain.member.model.dto.RoleSelectRequest;
import org.aibe4.dodeul.global.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class OnboardingApiController {

    @PostMapping("/role")
    public ResponseEntity<ApiResponse<Void>> selectRole(
            @RequestBody RoleSelectRequest request, HttpSession session) {
        session.setAttribute(AuthSessionKeys.SELECTED_ROLE, request.role());
        return ResponseEntity.ok(ApiResponse.success("역할 선택 완료", null));
    }
}

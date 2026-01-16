package org.aibe4.dodeul.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.dto.AuthSessionKeys;
import org.aibe4.dodeul.domain.member.model.dto.request.RoleSelectRequest;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원/마이페이지 API")
@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class OnboardingApiController {

    @Operation(summary = "역할 선택", description = "사용자가 선택한 역할을 세션에 저장합니다.")
    @MemberSwaggerDocs.SelectRole
    @PostMapping("/role")
    public CommonResponse<Void> selectRole(
        @RequestBody RoleSelectRequest request,
        HttpSession session
    ) {
        session.setAttribute(AuthSessionKeys.SELECTED_ROLE, request.role());
        return CommonResponse.success(SuccessCode.SUCCESS, null);
    }
}

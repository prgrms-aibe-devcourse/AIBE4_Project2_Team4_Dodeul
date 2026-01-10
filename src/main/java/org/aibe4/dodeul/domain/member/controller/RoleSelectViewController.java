package org.aibe4.dodeul.domain.member.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.dto.AuthSessionKeys;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/onboarding/role")
@RequiredArgsConstructor
public class RoleSelectViewController {

    @GetMapping
    public String page(
        @AuthenticationPrincipal CustomUserDetails user,
        HttpSession session
    ) {
        if (user != null) {
            return "redirect:/post-login"; // 로그인 상태면 post-login으로
        }

        Role selectedRole = (Role) session.getAttribute(AuthSessionKeys.SELECTED_ROLE);
        if (selectedRole != null) {
            return "redirect:/auth"; // 이미 role 선택했으면 다음 단계로
        }

        return "auth/role-select";
    }

    @PostMapping
    public String select(@RequestParam Role role, HttpSession session) {
        session.setAttribute(AuthSessionKeys.SELECTED_ROLE, role);
        return "redirect:/auth";
    }
}

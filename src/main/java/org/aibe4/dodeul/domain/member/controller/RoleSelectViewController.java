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
    public String page(@AuthenticationPrincipal CustomUserDetails user) {
        if (user != null) {
            return "redirect:/home";
        }
        return "auth/role-select";
    }

    @PostMapping
    public String select(@RequestParam Role role, HttpSession session) {
        session.setAttribute(AuthSessionKeys.SELECTED_ROLE, role);
        return "redirect:/auth";
    }
}

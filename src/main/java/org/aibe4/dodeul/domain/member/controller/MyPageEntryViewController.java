package org.aibe4.dodeul.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MyPageEntryViewController {

    @GetMapping("/mypage")
    public String entry(@AuthenticationPrincipal CustomUserDetails user) {
        if (user == null) {
            return "redirect:/auth/login";
        }

        Role role = user.getRole();
        if (role == Role.MENTOR) {
            return "redirect:/mentor/dashboard";
        }
        if (role == Role.MENTEE) {
            return "redirect:/mentee/dashboard";
        }

        // role이 비정상(null 등)인 경우: 온보딩으로
        return "redirect:/onboarding/role";
    }
}

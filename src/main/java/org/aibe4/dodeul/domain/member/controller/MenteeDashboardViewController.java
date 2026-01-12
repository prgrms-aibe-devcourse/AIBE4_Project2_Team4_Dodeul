package org.aibe4.dodeul.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MenteeDashboardViewController {

    private void assertMentee(CustomUserDetails user) {
        if (user.getRole() != Role.MENTEE) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
    }

    private void setCommonModel(Model model, String sidebarActive) {
        model.addAttribute("activeMenu", "mypage");
        model.addAttribute("sidebarActive", sidebarActive);
    }

    @GetMapping("/mentee/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        if (user == null) return "redirect:/auth/login";
        assertMentee(user);

        setCommonModel(model, "dashboard");
        return "mypage/mentee/dashboard";
    }

    @GetMapping("/mentee/profile")
    public String profile(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        if (user == null) return "redirect:/auth/login";
        assertMentee(user);

        setCommonModel(model, "profile");
        return "mypage/mentee/profile";
    }

    @GetMapping("/mentee/sessions")
    public String sessions(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        if (user == null) return "redirect:/auth/login";
        assertMentee(user);

        setCommonModel(model, "sessions");
        return "mypage/mentee/sessions";
    }

    @GetMapping("/mentee/reviews")
    public String reviews(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        if (user == null) return "redirect:/auth/login";
        assertMentee(user);

        setCommonModel(model, "reviews");
        return "mypage/mentee/reviews";
    }

    @GetMapping("/mentee/scraps")
    public String scraps(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        if (user == null) return "redirect:/auth/login";
        assertMentee(user);

        setCommonModel(model, "scraps");
        return "mypage/mentee/scraps";
    }
}

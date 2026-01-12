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
public class MentorDashboardViewController {

    private void assertMentor(CustomUserDetails user) {
        if (user.getRole() != Role.MENTOR) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
    }

    private void setCommonModel(Model model, String sidebarActive) {
        model.addAttribute("activeMenu", "mypage");
        model.addAttribute("sidebarActive", sidebarActive);
    }

    @GetMapping("/mentor/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        if (user == null) return "redirect:/auth/login";
        assertMentor(user);

        setCommonModel(model, "dashboard");
        return "mypage/mentor/dashboard";
    }

    @GetMapping("/mentor/profile")
    public String profile(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        if (user == null) return "redirect:/auth/login";
        assertMentor(user);

        setCommonModel(model, "profile");
        return "mypage/mentor/profile";
    }

    @GetMapping("/mentor/sessions")
    public String sessions(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        if (user == null) return "redirect:/auth/login";
        assertMentor(user);

        setCommonModel(model, "sessions");
        return "mypage/mentor/sessions";
    }

    @GetMapping("/mentor/scraps")
    public String scraps(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        if (user == null) return "redirect:/auth/login";
        assertMentor(user);

        setCommonModel(model, "scraps");
        return "mypage/mentor/scraps";
    }
}

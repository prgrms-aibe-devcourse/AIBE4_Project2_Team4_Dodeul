package org.aibe4.dodeul.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mentee")
@PreAuthorize("hasRole('MENTEE')")
public class MenteeDashboardViewController {

    private void setCommonModel(Model model, String sidebarActive) {
        model.addAttribute("activeMenu", "mypage");
        model.addAttribute("sidebarActive", sidebarActive);
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        setCommonModel(model, "dashboard");
        return "mypage/mentee/dashboard";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        setCommonModel(model, "profile");
        return "mypage/mentee/profile";
    }

    @GetMapping("/sessions")
    public String sessions(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        setCommonModel(model, "sessions");
        return "mypage/mentee/sessions";
    }

    @GetMapping("/reviews")
    public String reviews(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        setCommonModel(model, "reviews");
        return "mypage/mentee/reviews";
    }

    @GetMapping("/scraps")
    public String scraps(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        setCommonModel(model, "scraps");
        return "mypage/mentee/scraps";
    }

    @GetMapping("/profile/edit")
    public String editProfile(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        setCommonModel(model, "profile");
        return "mypage/mentee/profile-edit";
    }
}

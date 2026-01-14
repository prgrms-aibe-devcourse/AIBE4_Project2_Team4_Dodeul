package org.aibe4.dodeul.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.review.model.dto.ReviewResponse;
import org.aibe4.dodeul.domain.review.service.ReviewService;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mentor")
@PreAuthorize("hasRole('MENTOR')")
public class MentorDashboardViewController {

    private final ReviewService reviewService;

    private void setCommonModel(Model model, String sidebarActive) {
        model.addAttribute("activeMenu", "mypage");
        model.addAttribute("sidebarActive", sidebarActive);
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        setCommonModel(model, "dashboard");
        return "mypage/mentor/dashboard";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        setCommonModel(model, "profile");
        return "mypage/mentor/profile";
    }

    @GetMapping("/sessions")
    public String sessions(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        setCommonModel(model, "sessions");
        return "mypage/mentor/sessions";
    }

    @GetMapping("/reviews")
    public String reviews(@AuthenticationPrincipal CustomUserDetails user, Model model, @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        setCommonModel(model, "reviews");
        Page<ReviewResponse> reviewResponses = reviewService.getReceivedReviews(user.getMemberId(), pageable);
        model.addAttribute("reviewResponses", reviewResponses);
        return "mypage/mentee/reviews";
    }

    @GetMapping("/scraps")
    public String scraps(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        setCommonModel(model, "scraps");
        return "mypage/mentor/scraps";
    }

    @GetMapping("/profile/edit")
    public String editProfile(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        setCommonModel(model, "profile");
        return "mypage/mentor/profile-edit";
    }
}

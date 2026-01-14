package org.aibe4.dodeul.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.review.model.dto.ReviewFormDataDto;
import org.aibe4.dodeul.domain.review.service.ReviewService;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/new/{matchingId}")
    @PreAuthorize("@consultationGuard.isCorrectMatchedMemberAndRoomClosed(#matchingId, #userDetails.memberId)")
    public String reviewForm(@PathVariable Long matchingId, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        if (reviewService.hasReview(matchingId)) {
            model.addAttribute("errorMessage", "이미 작성된 리뷰가 존재합니다.");
            model.addAttribute("nextUrl", "/mentee/dashboard");

            return "error/access-denied";
        }

        ReviewFormDataDto reviewFormDataDto = reviewService.loadFormData(matchingId);
        model.addAttribute("reviewFormDataDto", reviewFormDataDto);

        return "review/review-form";
    }
}

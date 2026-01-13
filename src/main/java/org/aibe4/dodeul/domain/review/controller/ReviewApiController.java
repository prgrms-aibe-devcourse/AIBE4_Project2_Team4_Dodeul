package org.aibe4.dodeul.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.review.model.dto.ReviewRequest;
import org.aibe4.dodeul.domain.review.service.ReviewService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewApiController {

    private final ReviewService reviewService;

    @PostMapping("/{matchingId}")
    @PreAuthorize("@consultationGuard.isMenteeOfMatching(#matchingId, #userDetails.memberId)")
    public CommonResponse<Void> saveReview(@PathVariable Long matchingId, @ModelAttribute ReviewRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        reviewService.saveReview(matchingId, request);

        return CommonResponse.success(SuccessCode.CREATE_SUCCESS, null);
    }
}

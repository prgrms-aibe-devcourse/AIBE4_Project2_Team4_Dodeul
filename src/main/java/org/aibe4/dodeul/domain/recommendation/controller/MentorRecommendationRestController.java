package org.aibe4.dodeul.domain.recommendation.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.recommendation.model.dto.MentorRecommendationResponse;
import org.aibe4.dodeul.domain.recommendation.service.MentorRecommendationService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class MentorRecommendationRestController {

    private final MentorRecommendationService mentorRecommendationService;

    @PreAuthorize("hasRole('MENTEE')")
    @GetMapping("/mentors")
    public CommonResponse<List<MentorRecommendationResponse>> recommendMentors(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam Long applicationId) {

        List<MentorRecommendationResponse> responses = mentorRecommendationService.recommendMentors(
            userDetails.getMemberId(), applicationId);

        return CommonResponse.success(SuccessCode.SUCCESS, responses, "멘토 추천을 성공했습니다.");
    }
}

package org.aibe4.dodeul.domain.recommendation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Recommendation", description = "멘토 추천 API")
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class MentorRecommendationRestController {

    private final MentorRecommendationService mentorRecommendationService;

    @Operation(summary = "멘토 추천", description = "작성된 지원서(applicationId)를 기반으로 적합한 멘토를 추천")
    @MentorRecommendationSwaggerDocs.RecommendMentors
    @PreAuthorize("hasRole('MENTEE')")
    @GetMapping("/mentors")
    public CommonResponse<List<MentorRecommendationResponse>> recommendMentors(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Parameter(description = "상담신청서 ID", example = "1", required = true)
        @RequestParam Long applicationId) {

        List<MentorRecommendationResponse> responses = mentorRecommendationService.recommendMentors(
            userDetails.getMemberId(), applicationId);

        return CommonResponse.success(SuccessCode.SUCCESS, responses, "멘토 추천을 성공했습니다.");
    }
}

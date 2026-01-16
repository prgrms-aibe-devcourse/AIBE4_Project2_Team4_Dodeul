package org.aibe4.dodeul.domain.recommendation.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "멘토 추천 응답 DTO")
public class MentorRecommendationResponse {

    @Schema(description = "멘토 ID", example = "10")
    private Long mentorId;

    @Schema(description = "멘토 닉네임", example = "AI전문가")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/ai.jpg")
    private String profileUrl;

    @Schema(description = "직무", example = "AI Engineer")
    private String job;

    @Schema(description = "경력(년차)", example = "7")
    private int careerYears;

    @Schema(description = "기술 스택 목록", example = "[\"Python\", \"TensorFlow\"]")
    private List<String> skillTags;

    @Schema(description = "리뷰 추천 수", example = "42")
    private Long recommendedReviewCount;

    @Schema(description = "완료된 상담 수", example = "50")
    private Long completedMatchingCount;

    @Schema(description = "응답률 (%)", example = "100.0")
    private Double responseRate;

    @Schema(description = "매칭 적합도 점수 (0~100)", example = "98.5")
    private Double matchScore;
}

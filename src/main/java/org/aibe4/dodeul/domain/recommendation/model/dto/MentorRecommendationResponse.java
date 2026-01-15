package org.aibe4.dodeul.domain.recommendation.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MentorRecommendationResponse {
    private Long mentorId;
    private String nickname;
    private String profileUrl;
    private String job;
    private int careerYears;
    private List<String> skillTags;
    private Long recommendedReviewCount;
    private Long completedMatchingCount;
    private Double responseRate;
    private Double matchScore;
}

package org.aibe4.dodeul.domain.review.model.dto;

import lombok.Builder;
import lombok.Getter;
import org.aibe4.dodeul.domain.review.model.entity.Review;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponse {
    private Long reviewId;
    private String mentorNickname; // 멘토 이름
    private String menteeNickname; // 작성자(멘티) 이름
    private String content;
    private boolean isRecommended;
    private LocalDateTime createdAt;

    public static ReviewResponse of(Review review) {
        return ReviewResponse.builder()
            .reviewId(review.getId())
            .mentorNickname(review.getMentor().getNickname())
            .menteeNickname(review.getMentee().getNickname())
            .content(review.getContent())
            .isRecommended(review.isRecommended())
            .createdAt(review.getCreatedAt())
            .build();
    }
}

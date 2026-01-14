package org.aibe4.dodeul.domain.review.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewRequest {
    private String content;
    private boolean isRecommended;
}

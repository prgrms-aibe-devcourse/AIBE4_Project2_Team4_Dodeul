package org.aibe4.dodeul.domain.review.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewRequest {
    private String content;
    @JsonProperty("isRecommended")
    private boolean isRecommended;
}

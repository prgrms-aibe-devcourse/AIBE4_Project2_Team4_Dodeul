package org.aibe4.dodeul.domain.review.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.aibe4.dodeul.domain.matching.model.enums.MatchingStatus;

@Getter
@AllArgsConstructor
public class NotReviewdDto {
    private Long matchingId;
    private String mentorNickname;
    private MatchingStatus matchingStatus;
}

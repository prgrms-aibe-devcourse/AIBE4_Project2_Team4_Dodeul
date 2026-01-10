package org.aibe4.dodeul.domain.matching.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.aibe4.dodeul.domain.matching.model.enums.MatchingStatus;

@Getter
@AllArgsConstructor
public class MatchingStatusResponse {
    private Long matchingId;
    private MatchingStatus status;
}

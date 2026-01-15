package org.aibe4.dodeul.domain.matching.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.aibe4.dodeul.domain.matching.model.enums.MatchingStatus;

@Getter
@AllArgsConstructor
@Schema(description = "매싱 상태 응답 DTO")
public class MatchingStatusResponse {

    @Schema(description = "매칭 ID", example = "1")
    private Long matchingId;

    @Schema(description = "매칭 상태", example = "WAITING")
    private MatchingStatus status;
}

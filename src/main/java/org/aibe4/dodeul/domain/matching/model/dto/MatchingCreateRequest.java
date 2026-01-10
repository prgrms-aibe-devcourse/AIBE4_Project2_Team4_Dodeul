package org.aibe4.dodeul.domain.matching.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "매칭 생성 요청 DTO")
public class MatchingCreateRequest {

    @Schema(description = "상담 신청서 ID", example = "1")
    private Long applicationId;

    @Schema(description = "멘토 ID", example = "2")
    private Long mentorId;
}

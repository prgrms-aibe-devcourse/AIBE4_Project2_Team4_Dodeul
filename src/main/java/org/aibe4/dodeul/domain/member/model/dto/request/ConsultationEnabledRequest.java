package org.aibe4.dodeul.domain.member.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ConsultationEnabledRequest(
    @Schema(description = "상담 가능 여부", example = "true")
    @NotNull Boolean enabled
) {
}

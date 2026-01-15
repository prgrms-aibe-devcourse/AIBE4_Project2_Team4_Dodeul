package org.aibe4.dodeul.domain.member.model.dto.request;

import jakarta.validation.constraints.NotNull;

public record ConsultationEnabledRequest(
    @NotNull Boolean enabled
) {
}

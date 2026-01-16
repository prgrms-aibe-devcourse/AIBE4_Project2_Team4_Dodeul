package org.aibe4.dodeul.domain.member.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.aibe4.dodeul.domain.member.model.enums.Role;

public record RoleSelectRequest(
    @Schema(description = "선택한 역할(세션 저장)", example = "MENTOR")
    Role role
) {
}

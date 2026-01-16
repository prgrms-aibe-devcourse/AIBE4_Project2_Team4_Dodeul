package org.aibe4.dodeul.domain.member.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.aibe4.dodeul.domain.member.model.enums.Role;

public record RegisterRequest(
    @Schema(description = "이메일", example = "test@example.com")
    String email,

    @Schema(description = "비밀번호", example = "P@ssw0rd!")
    String password,

    @Schema(description = "비밀번호 확인", example = "P@ssw0rd!")
    String confirmPassword,

    @Schema(description = "역할", example = "MENTOR")
    Role role
) {
}

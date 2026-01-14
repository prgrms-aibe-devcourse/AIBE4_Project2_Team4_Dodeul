package org.aibe4.dodeul.domain.member.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
    @Email @NotBlank String email,
    @NotBlank String password
) {
}

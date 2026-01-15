package org.aibe4.dodeul.domain.member.model.dto.request;

import org.aibe4.dodeul.domain.member.model.enums.Role;

public record RegisterRequest(String email, String password, String confirmPassword, Role role) {
}

package org.aibe4.dodeul.domain.member.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record NicknameUpdateRequest(
    @Schema(description = "닉네임(2~10자, 한글/영문/숫자)", example = "yunwoo1214")
    String nickname
) {
}

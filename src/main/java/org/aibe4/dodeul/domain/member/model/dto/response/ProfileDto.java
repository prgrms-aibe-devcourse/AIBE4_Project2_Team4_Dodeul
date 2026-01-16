package org.aibe4.dodeul.domain.member.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "간단 프로필 DTO")
public record ProfileDto(
    @Schema(example = "3") Long memberId,
    @Schema(example = "1214") String nickname,
    @Schema(example = "안녕하세요", nullable = true) String intro,
    @Schema(example = "https://.../profile.png", nullable = true) String profileUrl,
    @Schema(example = "백엔드 개발자", nullable = true) String job
) {
    public static ProfileDto empty(Long memberId, String nickname) {
        return new ProfileDto(memberId, nickname, null, null, null);
    }
}

package org.aibe4.dodeul.domain.member.model.dto;

public record ProfileDto(
    Long memberId,
    String nickname,
    String intro,
    String profileUrl,
    String job
) {
    public static ProfileDto empty(Long memberId, String nickname) {
        return new ProfileDto(memberId, nickname, null, null, null);
    }
}

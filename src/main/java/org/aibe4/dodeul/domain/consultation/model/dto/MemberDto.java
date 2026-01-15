package org.aibe4.dodeul.domain.consultation.model.dto;

import lombok.Builder;
import lombok.Getter;
import org.aibe4.dodeul.domain.member.model.entity.Member;

@Getter
@Builder
public class MemberDto {

    private Long memberId;
    private String nickname;
    private String profileUrl;

    public static MemberDto of(Member member) {
        if (member == null) {
            return MemberDto.builder()
                .nickname("(알 수 없음)")
                .build();
        }

        String url = (member.getProfile() != null) ? member.getProfile().getProfileUrl() : null;

        return MemberDto.builder()
            .memberId(member.getId())
            .nickname(member.getNickname())
            .profileUrl(url)
            .build();
    }
}

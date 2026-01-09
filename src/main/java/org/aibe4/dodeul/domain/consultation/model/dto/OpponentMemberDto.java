package org.aibe4.dodeul.domain.consultation.model.dto;

import lombok.Builder;
import lombok.Getter;
import org.aibe4.dodeul.domain.member.model.entity.Member;

@Getter
@Builder
public class OpponentMemberDto {

    private String nickname;
    private String profileUrl;

    public static OpponentMemberDto of(Member member) {
        if (member == null) {
            return OpponentMemberDto.builder()
                    .nickname("(알 수 없음)")
                    //                .profileUrl(null) // 프로필 이미지가 없을 때 기본 이미지 설정 필요
                    .build();
        }

        return OpponentMemberDto.builder()
                .nickname(member.getNickname())
                //            .profileUrl(member.getProfileUrl())
                .build();
    }
}

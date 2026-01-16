package org.aibe4.dodeul.domain.member.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "멘티 내 프로필 조회 응답")
public class MenteeMyProfileResponse {

    @Schema(example = "3")
    private Long memberId;

    @Schema(example = "1214")
    private String nickname;

    @Schema(example = "https://.../profile.png", nullable = true)
    private String profileUrl;

    @Schema(example = "안녕하세요", nullable = true)
    private String intro;

    @Schema(example = "백엔드 개발자", nullable = true)
    private String job;

    @Schema(description = "스킬 태그 이름 목록", example = "[\"JPA\"]")
    private List<String> skillTags;

    @Schema(description = "상담 태그 목록(ENUM name)", example = "[\"CAREER\"]")
    private List<String> consultingTags;
}

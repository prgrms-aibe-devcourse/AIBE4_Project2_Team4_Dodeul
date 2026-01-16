package org.aibe4.dodeul.domain.member.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "멘토 프로필 수정 요청")
public class MentorProfileUpdateRequest {

    @Schema(description = "프로필 이미지 URL", example = "https://.../profile.png", nullable = true)
    private String profileUrl;

    @Schema(description = "자기소개", example = "안녕하세요. 멘토입니다.", nullable = true)
    private String intro;

    @Schema(description = "직무", example = "백엔드 개발자", nullable = true)
    private String job;

    @Schema(description = "연차", example = "3", nullable = true)
    private Integer careerYears;

    @Schema(description = "상담 가능 여부", example = "true", nullable = true)
    private Boolean consultationEnabled;

    @Schema(description = "스킬 태그 목록", example = "[\"Spring\",\"JPA\",\"MySQL\"]", nullable = true)
    private List<String> skillTags;

    @Schema(description = "상담 태그 목록(ENUM name)", example = "[\"CAREER\",\"RESUME\"]", nullable = true)
    private List<String> consultingTags; // 예: ["CAREER", "RESUME"]
}

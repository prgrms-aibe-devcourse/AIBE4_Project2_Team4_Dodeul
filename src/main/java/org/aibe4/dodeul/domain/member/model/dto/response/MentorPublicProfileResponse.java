package org.aibe4.dodeul.domain.member.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "멘토 공개 프로필 조회 응답")
public class MentorPublicProfileResponse {

    @Schema(example = "10")
    private Long mentorId;

    @Schema(example = "mentorKim")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://.../profile.png", nullable = true)
    private String profileUrl;

    @Schema(description = "직무", example = "백엔드 개발자", nullable = true)
    private String job;

    @Schema(description = "자기소개", example = "안녕하세요. 멘토입니다.", nullable = true)
    private String intro;

    @Schema(description = "연차", example = "5", nullable = true)
    private Integer careerYears;

    @Schema(description = "상담 가능 여부", example = "true", nullable = true)
    private Boolean consultationEnabled;

    @Schema(description = "스킬 태그", example = "[\"Spring\",\"JPA\"]")
    private List<String> skillTags;

    @Schema(description = "상담 가능 분야(ENUM name)", example = "[\"CAREER\",\"RESUME\"]")
    private List<String> consultingFields;
}

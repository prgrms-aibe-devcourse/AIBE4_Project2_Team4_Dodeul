package org.aibe4.dodeul.domain.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "스킬 태그 목록 응답")
public class SkillTagListResponse {

    @Schema(description = "스킬 태그 이름 목록", example = "[\"Java\", \"Spring\", \"Python\"]")
    private List<String> skillTags;

    public static SkillTagListResponse from(List<String> skillTagNames) {
        return new SkillTagListResponse(skillTagNames);
    }
}

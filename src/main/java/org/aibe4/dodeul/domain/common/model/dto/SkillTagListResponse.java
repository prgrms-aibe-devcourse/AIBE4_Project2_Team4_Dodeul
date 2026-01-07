package org.aibe4.dodeul.domain.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SkillTagListResponse {

    private List<String> skillTags;

    public static SkillTagListResponse from(List<String> skillTagNames) {
        return new SkillTagListResponse(skillTagNames);
    }
}

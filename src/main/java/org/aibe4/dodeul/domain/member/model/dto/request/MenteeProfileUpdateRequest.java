package org.aibe4.dodeul.domain.member.model.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MenteeProfileUpdateRequest {
    private String profileUrl;
    private String intro;
    private String job;

    private List<String> skillTags;
    private List<String> consultingTags;
}

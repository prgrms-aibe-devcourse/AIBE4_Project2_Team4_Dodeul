package org.aibe4.dodeul.domain.member.model.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MenteeMyProfileResponse {

    private Long memberId;
    private String nickname;

    private String profileUrl;
    private String intro;
    private String job;

    private List<String> skillTags;
    private List<String> consultingTags;
}

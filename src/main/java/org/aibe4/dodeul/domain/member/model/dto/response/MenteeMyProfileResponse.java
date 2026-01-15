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

    // 스킬 태그 이름 목록
    private List<String> skillTags;

    // ConsultingTag enum name 목록 (CAREER, RESUME...)
    private List<String> consultingTags;
}

package org.aibe4.dodeul.domain.member.model.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MentorPublicProfileResponse {

    private Long mentorId;
    private String nickname;

    // MentorProfile.profileUrl
    private String profileUrl;

    // MentorProfile.job (현재 String)
    private String job;

    // MentorProfile.intro / careerYears / consultationEnabled
    private String intro;
    private Integer careerYears;
    private Boolean consultationEnabled;

    // MemberSkillTag -> SkillTag.name
    private List<String> skillTags;

    // MemberConsultingTag -> ConsultingTag.name()
    private List<String> consultingFields;
}

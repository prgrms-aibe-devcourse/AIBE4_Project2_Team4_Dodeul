package org.aibe4.dodeul.domain.search.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.aibe4.dodeul.domain.matching.MatchingConstants;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.entity.MemberConsultingTag;
import org.aibe4.dodeul.domain.member.model.entity.MentorProfile;
import org.aibe4.dodeul.domain.search.model.enums.MentorConsultationAvailableStatus;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorSearchResponse {
    private Long memberId;
    private String nickname;
    private String profileUrl;
    private String job;
    private int careerYears;
    private List<String> skillTags;
    private List<ConsultingTag> consultingTags;
    private Long recommendCount;
    private Long completedMatchingCount;
    private Double responseRate;
    private MentorConsultationAvailableStatus status;

    public static MentorSearchResponse from(Member member, long activeMatchingCount) {
        MentorProfile profile = member.getMentorProfile();

        MentorConsultationAvailableStatus status;
        if (!profile.isConsultationEnabled()) {
            status = MentorConsultationAvailableStatus.OFF;
        } else if (activeMatchingCount >= MatchingConstants.MAX_ACTIVE_MATCHING_COUNT) {
            status = MentorConsultationAvailableStatus.FULL;
        } else {
            status = MentorConsultationAvailableStatus.AVAILABLE;
        }

        return MentorSearchResponse.builder()
            .memberId(member.getId())
            .nickname(member.getNickname())
            .profileUrl(profile.getProfileUrl())
            .job(profile.getJob())
            .careerYears(profile.getCareerYears())
            .skillTags(member.getSkillTags().stream()
                .map(mst -> mst.getSkillTag().getName()).toList())
            .consultingTags(member.getConsultingTags().stream()
                .map(MemberConsultingTag::getConsultingTag).toList())
            .recommendCount(profile.getRecommendCount())
            .completedMatchingCount(profile.getCompletedMatchingCount())
            .responseRate(profile.getResponseRate())
            .status(status)
            .build();
    }
}

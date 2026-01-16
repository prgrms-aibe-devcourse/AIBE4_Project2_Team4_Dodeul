package org.aibe4.dodeul.domain.search.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "멘토 검색 응답 DTO")
public class MentorSearchResponse {

    @Schema(description = "멘토 회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "멘토 닉네임", example = "코딩하는라이언")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileUrl;

    @Schema(description = "직무", example = "Backend Developer")
    private String job;

    @Schema(description = "경력(년차)", example = "5")
    private int careerYears;

    @Schema(description = "자기소개", example = "백엔드를 정말 잘합니다.")
    private String intro;

    @Schema(description = "기술 스택 목록", example = "[\"Spring Boot\", \"JPA\", \"Docker\"]")
    private List<String> skillTags;

    @Schema(description = "상담 가능 분야 목록", example = "[\"CAREER\", \"RESUME\"]")
    private List<ConsultingTag> consultingTags;

    @Schema(description = "리뷰 추천 수", example = "15")
    private Long recommendCount;

    @Schema(description = "완료된 상담 수", example = "23")
    private Long completedMatchingCount;

    @Schema(description = "응답률 (%)", example = "98.5")
    private Double responseRate;

    @Schema(description = "상담 가능 상태 (AVAILABLE, FULL, OFF)", example = "AVAILABLE")
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
            .intro(profile.getIntro())
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

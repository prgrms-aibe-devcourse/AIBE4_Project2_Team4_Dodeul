package org.aibe4.dodeul.domain.member.model.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class MentorCandidateDto {

    private Long id;
    private String nickname;
    private String job;
    private int careerYears;
    private String profileUrl;
    private double responseRate;
    private long recommendCount;
    private long completedMatchingCount;

    private List<String> skillTags = new ArrayList<>();
    private List<ConsultingTag> consultingTags = new ArrayList<>();

    @QueryProjection
    public MentorCandidateDto(Long id, String nickname, String job, int careerYears, String profileUrl, double responseRate, long recommendCount, long completedMatchingCount) {
        this.id = id;
        this.nickname = nickname;
        this.job = job;
        this.careerYears = careerYears;
        this.profileUrl = profileUrl;
        this.responseRate = responseRate;
        this.recommendCount = recommendCount;
        this.completedMatchingCount = completedMatchingCount;
    }
}

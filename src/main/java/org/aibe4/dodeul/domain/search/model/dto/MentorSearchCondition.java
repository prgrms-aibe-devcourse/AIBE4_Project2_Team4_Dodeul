package org.aibe4.dodeul.domain.search.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.aibe4.dodeul.domain.search.model.enums.SortType;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "멘토 검색 조건")
public class MentorSearchCondition {

    @Schema(description = "검색어 (멘토 닉네임)", example = "코딩하는라이언")
    private String keyword;

    @Schema(description = "직무 목록", example = "[\"Backend Developer\", \"Frontend Developer\"]")
    private List<String> jobs;

    @Schema(description = "기술 스택 목록", example = "[\"Java\", \"React\"]")
    private List<String> skillTags;

    @Schema(description = "상담 분야 목록", example = "[\"CAREER\", \"RESUME\"]")
    private List<ConsultingTag> consultingTags;

    @Schema(description = "상담 가능한 멘토만 보기", example = "true")
    private Boolean onlyAvailable;

    @Schema(description = "정렬 기준 (LATEST: 최신순, RECOMMEND: 추천순, MATCHING: 상담 많은 순, RESPONSE: 응답 빠른 순", example = "POPULAR")
    private SortType sortType;
}

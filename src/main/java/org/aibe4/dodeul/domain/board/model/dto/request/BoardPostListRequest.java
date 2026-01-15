package org.aibe4.dodeul.domain.board.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "게시글 목록 조회 요청 (필터)")
public class BoardPostListRequest {

    @Schema(description = "상담 분야(카테고리)", example = "CAREER")
    private ConsultingTag consultingTag;

    @Schema(description = "스킬 태그 ID 목록", example = "[1, 2]")
    private List<Long> tagIds;

    @Schema(
        description = "게시글 상태(ALL/OPEN/CLOSED) (없거나 잘못되면 OPEN으로 처리)",
        example = "OPEN")
    private String status;

    @Schema(description = "검색 키워드", example = "JPA")
    private String keyword;

    @Schema(description = "정렬 기준(LATEST/VIEWS/SCRAPS)", example = "LATEST")
    private String sort;
}

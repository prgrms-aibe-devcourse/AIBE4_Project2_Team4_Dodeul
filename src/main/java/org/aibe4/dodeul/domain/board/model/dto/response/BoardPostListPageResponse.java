package org.aibe4.dodeul.domain.board.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BoardPostListPageResponse {

    @Schema(description = "목록 데이터")
    private final List<BoardPostListResponse> content;

    @Schema(description = "현재 페이지(0부터 시작)")
    private final int page;

    @Schema(description = "페이지 크기")
    private final int size;

    @Schema(description = "전체 요소 수")
    private final long totalElements;

    @Schema(description = "전체 페이지 수")
    private final int totalPages;

    @Schema(description = "첫 페이지 여부")
    private final boolean first;

    @Schema(description = "마지막 페이지 여부")
    private final boolean last;

    public static BoardPostListPageResponse from(Page<BoardPostListResponse> pageResult) {
        return new BoardPostListPageResponse(
            pageResult.getContent(),
            pageResult.getNumber(),
            pageResult.getSize(),
            pageResult.getTotalElements(),
            pageResult.getTotalPages(),
            pageResult.isFirst(),
            pageResult.isLast());
    }
}

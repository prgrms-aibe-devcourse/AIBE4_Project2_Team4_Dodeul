package org.aibe4.dodeul.domain.board.model.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostListResponse;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BoardPostListPageResponse {

    private final List<BoardPostListResponse> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean first;
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

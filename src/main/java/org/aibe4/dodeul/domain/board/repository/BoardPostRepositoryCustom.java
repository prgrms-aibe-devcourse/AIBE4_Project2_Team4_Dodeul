package org.aibe4.dodeul.domain.board.repository;

import org.aibe4.dodeul.domain.board.model.dto.BoardPostListRequest;
import org.aibe4.dodeul.domain.board.model.dto.BoardPostListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardPostRepositoryCustom {
    Page<BoardPostListResponse> findPosts(
            BoardPostListRequest request, Long memberId, Pageable pageable);
}

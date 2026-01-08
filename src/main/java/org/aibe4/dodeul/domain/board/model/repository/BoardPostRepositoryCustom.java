// src/main/java/org/aibe4/dodeul/domain/board/model/repository/BoardPostRepositoryCustom.java
package org.aibe4.dodeul.domain.board.model.repository;

import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostListRequest;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardPostRepositoryCustom {

    Page<BoardPostListResponse> findPosts(
            BoardPostListRequest request, Long memberId, Pageable pageable);
}

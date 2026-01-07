// src/main/java/org/aibe4/dodeul/domain/board/service/BoardPostService.java
package org.aibe4.dodeul.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.BoardPostListRequest;
import org.aibe4.dodeul.domain.board.model.dto.BoardPostListResponse;
import org.aibe4.dodeul.domain.board.repository.BoardPostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardPostService {

    private final BoardPostRepository boardPostRepository;

    public Page<BoardPostListResponse> getPosts(
            BoardPostListRequest request, Long memberId, Pageable pageable) {
        return boardPostRepository.findPosts(request, memberId, pageable);
    }
}

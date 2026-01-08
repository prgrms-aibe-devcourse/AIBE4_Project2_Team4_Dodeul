// src/main/java/org/aibe4/dodeul/domain/board/service/BoardPostDetailService.java
package org.aibe4.dodeul.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostDetailResponse;
import org.aibe4.dodeul.domain.board.model.entity.BoardPost;
import org.aibe4.dodeul.domain.board.model.repository.BoardPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardPostDetailService {

    private final BoardPostRepository boardPostRepository;

    public BoardPostDetailResponse getDetail(Long postId) {
        BoardPost post =
                boardPostRepository
                        .findDetailById(postId)
                        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 인증 연동 전까지는 false 고정
        return BoardPostDetailResponse.from(post, false);
    }
}

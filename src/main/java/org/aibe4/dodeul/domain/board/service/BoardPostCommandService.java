// src/main/java/org/aibe4/dodeul/domain/board/service/BoardPostCommandService.java
package org.aibe4.dodeul.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.exception.BoardPolicyException;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostUpdateRequest;
import org.aibe4.dodeul.domain.board.model.entity.BoardPost;
import org.aibe4.dodeul.domain.board.model.enums.PostStatus;
import org.aibe4.dodeul.domain.board.model.repository.BoardPostRepository;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardPostCommandService {

    private final BoardPostRepository boardPostRepository;

    @Transactional
    public void update(Long postId, Long memberId, BoardPostUpdateRequest request) {
        if (memberId == null) {
            throw new BoardPolicyException(ErrorCode.UNAUTHORIZED_ACCESS, "로그인이 필요합니다.");
        }

        BoardPost post =
            boardPostRepository
                .findByIdAndPostStatusNot(postId, PostStatus.DELETED)
                .orElseThrow(
                    () -> new BoardPolicyException(ErrorCode.RESOURCE_NOT_FOUND, "게시글을 찾을 수 없습니다."));

        if (!Objects.equals(post.getMemberId(), memberId)) {
            throw new BoardPolicyException(ErrorCode.ACCESS_DENIED, "작성자만 수정할 수 있습니다.");
        }

        String title = normalize(request.getTitle());
        String content = normalize(request.getContent());

        if (title.isBlank()) {
            throw new BoardPolicyException(ErrorCode.INVALID_INPUT_VALUE, "제목은 공백일 수 없습니다.");
        }
        if (content.isBlank()) {
            throw new BoardPolicyException(ErrorCode.INVALID_INPUT_VALUE, "내용은 공백일 수 없습니다.");
        }

        post.update(title, content, request.getConsultingTag());
    }

    @Transactional
    public void delete(Long postId, Long memberId) {
        if (memberId == null) {
            throw new BoardPolicyException(ErrorCode.UNAUTHORIZED_ACCESS, "로그인이 필요합니다.");
        }

        BoardPost post =
            boardPostRepository
                .findByIdAndPostStatusNot(postId, PostStatus.DELETED)
                .orElseThrow(
                    () -> new BoardPolicyException(ErrorCode.RESOURCE_NOT_FOUND, "게시글을 찾을 수 없습니다."));

        if (!Objects.equals(post.getMemberId(), memberId)) {
            throw new BoardPolicyException(ErrorCode.ACCESS_DENIED, "작성자만 삭제할 수 있습니다.");
        }

        post.delete();
    }

    private String normalize(String v) {
        return v == null ? "" : v.trim();
    }
}

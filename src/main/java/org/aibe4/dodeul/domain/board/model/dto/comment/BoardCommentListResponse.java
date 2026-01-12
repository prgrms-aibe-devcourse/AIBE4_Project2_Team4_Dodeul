package org.aibe4.dodeul.domain.board.model.dto.comment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BoardCommentListResponse {

    private Long postId;
    private Long acceptedCommentId;
    private List<BoardCommentItemResponse> comments;

    public static BoardCommentListResponse of(
        Long postId, Long acceptedCommentId, List<BoardCommentItemResponse> comments) {
        return new BoardCommentListResponse(postId, acceptedCommentId, comments);
    }
}

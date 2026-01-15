package org.aibe4.dodeul.domain.board.model.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "댓글 목록 응답")
public class BoardCommentListResponse {

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "채택된 댓글 ID (없으면 null)", example = "5")
    private Long acceptedCommentId;

    @Schema(description = "댓글 목록 (계층 구조)")
    private List<BoardCommentItemResponse> comments;

    public static BoardCommentListResponse of(
        Long postId, Long acceptedCommentId, List<BoardCommentItemResponse> comments) {
        return new BoardCommentListResponse(postId, acceptedCommentId, comments);
    }
}

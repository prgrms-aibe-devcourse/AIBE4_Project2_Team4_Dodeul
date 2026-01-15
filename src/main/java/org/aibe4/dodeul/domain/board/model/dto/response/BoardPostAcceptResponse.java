// src/main/java/org/aibe4/dodeul/domain/board/model/dto/response/BoardPostAcceptResponse.java
package org.aibe4.dodeul.domain.board.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "댓글 채택 응답")
public class BoardPostAcceptResponse {

    @Schema(description = "게시글 ID", example = "1")
    private final Long postId;

    @Schema(description = "채택된 댓글 ID", example = "5")
    private final Long acceptedCommentId;

    @Schema(description = "게시글 상태 (채택 후 CLOSED)", example = "CLOSED")
    private final String postStatus;

    @Builder
    private BoardPostAcceptResponse(Long postId, Long acceptedCommentId, String postStatus) {
        this.postId = postId;
        this.acceptedCommentId = acceptedCommentId;
        this.postStatus = postStatus;
    }

    public static BoardPostAcceptResponse of(Long postId, Long acceptedCommentId, String postStatus) {
        return BoardPostAcceptResponse.builder()
            .postId(postId)
            .acceptedCommentId(acceptedCommentId)
            .postStatus(postStatus)
            .build();
    }
}

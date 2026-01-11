// src/main/java/org/aibe4/dodeul/domain/board/model/dto/response/BoardPostAcceptResponse.java
package org.aibe4.dodeul.domain.board.model.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BoardPostAcceptResponse {

    private final Long postId;
    private final Long acceptedCommentId;
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

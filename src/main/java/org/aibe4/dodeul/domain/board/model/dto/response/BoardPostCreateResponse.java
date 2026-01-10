package org.aibe4.dodeul.domain.board.model.dto.response;

import lombok.Getter;

@Getter
public class BoardPostCreateResponse {

    private final Long postId;

    public BoardPostCreateResponse(Long postId) {
        this.postId = postId;
    }
}

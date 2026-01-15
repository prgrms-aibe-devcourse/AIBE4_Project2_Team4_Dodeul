// src/main/java/org/aibe4/dodeul/domain/board/model/dto/response/BoardPostCreateResponse.java
package org.aibe4.dodeul.domain.board.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "게시글 생성 응답")
public class BoardPostCreateResponse {

    @Schema(description = "생성된 게시글 ID", example = "1")
    private final Long postId;

    public BoardPostCreateResponse(Long postId) {
        this.postId = postId;
    }
}

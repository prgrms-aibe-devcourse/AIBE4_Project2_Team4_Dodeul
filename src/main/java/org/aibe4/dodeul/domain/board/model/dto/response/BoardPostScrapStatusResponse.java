// src/main/java/org/aibe4/dodeul/domain/board/model/dto/response/BoardPostScrapStatusResponse.java
package org.aibe4.dodeul.domain.board.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "게시글 스크랩 상태 응답")
public class BoardPostScrapStatusResponse {

    @Schema(description = "게시글 ID", example = "1")
    private final Long postId;

    @Schema(description = "내가 스크랩했는지 여부", example = "true")
    private final boolean scrappedByMe;

    @Schema(description = "전체 스크랩 수", example = "5")
    private final long scrapCount;

    private BoardPostScrapStatusResponse(Long postId, boolean scrappedByMe, long scrapCount) {
        this.postId = postId;
        this.scrappedByMe = scrappedByMe;
        this.scrapCount = scrapCount;
    }

    public static BoardPostScrapStatusResponse of(Long postId, boolean scrappedByMe, long scrapCount) {
        return new BoardPostScrapStatusResponse(postId, scrappedByMe, scrapCount);
    }
}

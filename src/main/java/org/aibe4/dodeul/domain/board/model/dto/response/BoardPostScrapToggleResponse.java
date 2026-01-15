// src/main/java/org/aibe4/dodeul/domain/board/model/dto/response/BoardPostScrapToggleResponse.java
package org.aibe4.dodeul.domain.board.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "게시글 스크랩 토글 응답")
public class BoardPostScrapToggleResponse {

    @Schema(description = "게시글 ID", example = "1")
    private final Long postId;

    @Schema(description = "토글 후 내가 스크랩했는지 여부", example = "true")
    private final boolean scrappedByMe;

    @Schema(description = "토글 후 전체 스크랩 수", example = "6")
    private final long scrapCount;

    private BoardPostScrapToggleResponse(Long postId, boolean scrappedByMe, long scrapCount) {
        this.postId = postId;
        this.scrappedByMe = scrappedByMe;
        this.scrapCount = scrapCount;
    }

    public static BoardPostScrapToggleResponse of(Long postId, boolean scrappedByMe, long scrapCount) {
        return new BoardPostScrapToggleResponse(postId, scrappedByMe, scrapCount);
    }
}

// src/main/java/org/aibe4/dodeul/domain/board/model/dto/response/BoardPostScrapToggleResponse.java
package org.aibe4.dodeul.domain.board.model.dto.response;

import lombok.Getter;

@Getter
public class BoardPostScrapToggleResponse {

    private final Long postId;
    private final boolean scrappedByMe;
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

// src/main/java/org/aibe4/dodeul/domain/board/model/dto/response/BoardPostScrapStatusResponse.java
package org.aibe4.dodeul.domain.board.model.dto.response;

import lombok.Getter;

@Getter
public class BoardPostScrapStatusResponse {

    private final Long postId;
    private final boolean scrappedByMe;
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

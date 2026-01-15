// src/main/java/org/aibe4/dodeul/domain/board/model/dto/response/MyScrapListResponse.java
package org.aibe4.dodeul.domain.board.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "내 스크랩 목록 응답")
public class MyScrapListResponse {

    @Schema(description = "스크랩 항목 목록")
    private final List<MyScrapItemResponse> items;

    private MyScrapListResponse(List<MyScrapItemResponse> items) {
        this.items = items;
    }

    public static MyScrapListResponse of(List<MyScrapItemResponse> items) {
        return new MyScrapListResponse(items);
    }
}

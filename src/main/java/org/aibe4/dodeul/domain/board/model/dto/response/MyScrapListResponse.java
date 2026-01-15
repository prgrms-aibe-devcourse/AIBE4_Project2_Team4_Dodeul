// src/main/java/org/aibe4/dodeul/domain/board/model/dto/response/MyScrapListResponse.java
package org.aibe4.dodeul.domain.board.model.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class MyScrapListResponse {

    private final List<MyScrapItemResponse> items;

    private MyScrapListResponse(List<MyScrapItemResponse> items) {
        this.items = items;
    }

    public static MyScrapListResponse of(List<MyScrapItemResponse> items) {
        return new MyScrapListResponse(items);
    }
}

// src/main/java/org/aibe4/dodeul/domain/board/model/dto/response/MyScrapItemResponse.java
package org.aibe4.dodeul.domain.board.model.dto.response;

import lombok.Getter;
import java.util.List;

@Getter
public class MyScrapItemResponse {

    private final Long id;
    private final String type;
    private final String title;
    private final String subText;
    private final List<String> tags;

    private MyScrapItemResponse(Long id, String type, String title, String subText, List<String> tags) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.subText = subText;
        this.tags = tags;
    }

    public static MyScrapItemResponse of(Long id, String type, String title, String subText, List<String> tags) {
        return new MyScrapItemResponse(id, type, title, subText, tags);
    }
}

// src/main/java/org/aibe4/dodeul/domain/board/model/dto/response/MyScrapItemResponse.java
package org.aibe4.dodeul.domain.board.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "내 스크랩 항목")
public class MyScrapItemResponse {

    @Schema(description = "항목 ID (게시글 ID 등)", example = "1")
    private final Long id;

    @Schema(description = "항목 타입 (post: 게시글)", example = "post")
    private final String type;

    @Schema(description = "제목", example = "Spring JPA N+1 문제 해결 방법")
    private final String title;

    @Schema(description = "부가 정보 (작성자, 날짜 등)", example = "작성자: 홍길동 · 2026-01-15")
    private final String subText;

    @Schema(description = "스킬 태그 목록", example = "[\"Java\", \"Spring\", \"JPA\"]")
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

// src/main/java/org/aibe4/dodeul/domain/board/model/dto/request/BoardPostFileCreateRequest.java
package org.aibe4.dodeul.domain.board.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "게시글 첨부파일 생성 요청")
public class BoardPostFileCreateRequest {

    @Valid
    @NotEmpty(message = "파일 목록은 비어 있을 수 없습니다.")
    @Schema(description = "첨부파일 목록")
    private List<Item> files;

    public static BoardPostFileCreateRequest of(List<Item> files) {
        return new BoardPostFileCreateRequest(files);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Schema(description = "개별 첨부파일 정보")
    public static class Item {

        @NotBlank(message = "파일 URL은 필수입니다.")
        @Schema(description = "파일 URL", example = "https://...")
        private String fileUrl;

        @NotBlank(message = "원본 파일명은 필수입니다.")
        @Schema(description = "원본 파일명", example = "image.png")
        private String originFileName;

        @NotBlank(message = "콘텐츠 타입은 필수입니다.")
        @Schema(description = "MIME 타입", example = "image/png")
        private String contentType;

        @PositiveOrZero(message = "파일 크기는 0 이상이어야 합니다.")
        @Schema(description = "파일 크기(byte)", example = "1024")
        private Long fileSize;

        public static Item of(
            String fileUrl, String originFileName, String contentType, Long fileSize) {
            return new Item(fileUrl, originFileName, contentType, fileSize);
        }
    }
}

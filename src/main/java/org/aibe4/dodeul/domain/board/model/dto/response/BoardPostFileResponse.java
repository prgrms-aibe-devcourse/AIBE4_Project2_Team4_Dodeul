// src/main/java/org/aibe4/dodeul/domain/board/model/dto/response/BoardPostFileResponse.java
package org.aibe4.dodeul.domain.board.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.aibe4.dodeul.domain.common.model.entity.CommonFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "게시글 첨부파일 응답")
public class BoardPostFileResponse {

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "첨부파일 목록")
    private List<FileItem> files;

    public static BoardPostFileResponse of(Long postId, List<CommonFile> files) {
        return new BoardPostFileResponse(postId, files.stream().map(FileItem::from).toList());
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Schema(description = "첨부파일 항목")
    public static class FileItem {

        @Schema(description = "파일 ID", example = "5")
        private Long id;

        @Schema(description = "파일 URL", example = "https://storage.example.com/files/abc123.pdf")
        private String fileUrl;

        @Schema(description = "원본 파일명", example = "이력서.pdf")
        private String originFileName;

        @Schema(description = "콘텐츠 타입", example = "application/pdf")
        private String contentType;

        @Schema(description = "파일 크기 (bytes)", example = "1048576")
        private Long fileSize;

        @Schema(description = "생성 시각", example = "2026-01-10T12:34:56")
        private LocalDateTime createdAt;

        public static FileItem from(CommonFile f) {
            return new FileItem(
                f.getId(),
                f.getFileUrl(),
                f.getOriginFileName(),
                f.getContentType(),
                f.getFileSize(),
                f.getCreatedAt());
        }
    }
}

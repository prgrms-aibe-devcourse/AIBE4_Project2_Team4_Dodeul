package org.aibe4.dodeul.domain.board.model.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.aibe4.dodeul.domain.common.model.entity.CommonFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BoardPostFileResponse {

    private Long postId;
    private List<FileItem> files;

    public static BoardPostFileResponse of(Long postId, List<CommonFile> files) {
        return new BoardPostFileResponse(postId, files.stream().map(FileItem::from).toList());
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FileItem {

        private Long id;
        private String fileUrl;
        private String originFileName;
        private String contentType;
        private Long fileSize;
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

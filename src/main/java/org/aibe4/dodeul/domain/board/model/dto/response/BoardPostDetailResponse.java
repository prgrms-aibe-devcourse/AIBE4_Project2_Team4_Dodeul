// src/main/java/org/aibe4/dodeul/domain/board/model/dto/response/BoardPostDetailResponse.java
package org.aibe4.dodeul.domain.board.model.dto.response;

import lombok.Builder;
import org.aibe4.dodeul.domain.board.model.entity.BoardPost;
import org.aibe4.dodeul.domain.board.model.enums.PostStatus;
import org.aibe4.dodeul.domain.common.model.entity.CommonFile;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record BoardPostDetailResponse(
    Long postId,
    String title,
    String content,
    String authorDisplayName,
    ConsultingTag consultingTag,
    PostStatus status,
    Boolean scrappedByMe,
    Integer viewCount,
    Integer scrapCount,
    Integer commentCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Boolean mine,
    Boolean canEdit,
    Boolean canDelete,
    List<BoardPostFileResponse> files,
    List<String> skillTags) {

    @Builder
    public record BoardPostFileResponse(String fileName, String fileUrl, String contentType, Long size) {
    }

    public static BoardPostDetailResponse from(
        BoardPost post,
        String authorDisplayName,
        boolean scrappedByMe,
        boolean mine,
        List<CommonFile> files) {

        List<BoardPostFileResponse> fileResponses =
            files == null
                ? List.of()
                : files.stream()
                .map(
                    f ->
                        BoardPostFileResponse.builder()
                            .fileName(f.getOriginFileName())
                            .fileUrl(f.getFileUrl())
                            .contentType(f.getContentType())
                            .size(f.getFileSize())
                            .build())
                .toList();

        boolean deleted = post.getPostStatus() == PostStatus.DELETED;

        List<String> skillTagNames = post.getSkillTags().stream()
            .map(SkillTag::getName)
            .toList();

        return BoardPostDetailResponse.builder()
            .postId(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .authorDisplayName(authorDisplayName)
            .consultingTag(post.getBoardConsulting())
            .status(post.getPostStatus())
            .scrappedByMe(scrappedByMe)
            .viewCount(post.getViewCount())
            .scrapCount(post.getScrapCount())
            .commentCount(post.getCommentCount())
            .createdAt(post.getCreatedAt())
            .updatedAt(post.getUpdatedAt())
            .mine(mine)
            .canEdit(mine && !deleted)
            .canDelete(mine && !deleted)
            .files(fileResponses)
            .skillTags(skillTagNames)
            .build();
    }
}

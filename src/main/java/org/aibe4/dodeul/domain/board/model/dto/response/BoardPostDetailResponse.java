// src/main/java/org/aibe4/dodeul/domain/board/model/dto/response/BoardPostDetailResponse.java
package org.aibe4.dodeul.domain.board.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.aibe4.dodeul.domain.board.model.entity.BoardPost;
import org.aibe4.dodeul.domain.board.model.enums.PostStatus;
import org.aibe4.dodeul.domain.common.model.entity.CommonFile;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Schema(description = "게시글 상세 정보 응답")
public record BoardPostDetailResponse(
    @Schema(description = "게시글 ID", example = "1") Long postId,
    @Schema(description = "제목", example = "JPA N+1 문제 질문합니다.") String title,
    @Schema(description = "본문", example = "안녕하세요. JPA를 사용하는데...") String content,
    @Schema(description = "작성자 닉네임", example = "개발꿈나무") String authorDisplayName,
    @Schema(description = "상담 분야") ConsultingTag consultingTag,
    @Schema(description = "게시글 상태", example = "OPEN") PostStatus status,
    @Schema(description = "내가 스크랩했는지 여부", example = "true") Boolean scrappedByMe,
    @Schema(description = "조회수", example = "128") Integer viewCount,
    @Schema(description = "스크랩 수", example = "12") Integer scrapCount,
    @Schema(description = "댓글 수", example = "5") Integer commentCount,
    @Schema(description = "작성 시각", example = "2026-01-10T10:00:00") LocalDateTime createdAt,
    @Schema(description = "최종 수정 시각", example = "2026-01-11T14:30:00") LocalDateTime updatedAt,
    @Schema(description = "내가 작성한 글인지 여부", example = "false") Boolean mine,
    @Schema(description = "수정 가능 여부", example = "false") Boolean canEdit,
    @Schema(description = "삭제 가능 여부", example = "false") Boolean canDelete,
    @Schema(description = "첨부파일 목록") List<BoardPostFileResponse> files,
    @Schema(description = "스킬 태그 목록", example = "[\"JPA\", \"N+1\", \"성능최적화\"]") List<String> skillTags) {

    @Builder
    @Schema(description = "게시글 첨부파일 정보")
    public record BoardPostFileResponse(
        @Schema(description = "원본 파일명", example = "erd.png") String fileName,
        @Schema(description = "파일 접근 URL", example = "https://.../erd.png") String fileUrl,
        @Schema(description = "파일 MIME 타입", example = "image/png") String contentType,
        @Schema(description = "파일 크기 (bytes)", example = "102400") Long size) {
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

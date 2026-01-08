// src/main/java/org/aibe4/dodeul/domain/board/model/dto/response/BoardPostDetailResponse.java
package org.aibe4.dodeul.domain.board.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.aibe4.dodeul.domain.board.model.entity.BoardPost;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BoardPostDetailResponse {

    private Long postId;
    private Author author;
    private ConsultingTagSummary consultingTag;

    private String title;
    private String content;
    private String postStatus;

    private long viewCount;
    private long scrapCount;
    private long commentCount;

    private LocalDateTime lastCommentedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private boolean scrappedByMe;

    private List<SkillTagSummary> skillTags;
    private List<AttachmentSummary> attachments;

    public static BoardPostDetailResponse from(BoardPost post, boolean scrappedByMe) {
        ConsultingTag consulting = post.getBoardConsulting();
        String consultingCode = consulting != null ? consulting.name() : null;
        String consultingName = consulting != null ? consulting.name() : null;

        List<SkillTagSummary> tags =
                post.getSkillTags().stream().map(SkillTagSummary::from).toList();

        return new BoardPostDetailResponse(
                post.getId(),
                new Author("작성자"),
                new ConsultingTagSummary(consultingCode, consultingName),
                post.getTitle(),
                post.getContent(),
                post.getPostStatus().name(),
                post.getViewCount() != null ? post.getViewCount().longValue() : 0L,
                post.getScrapCount() != null ? post.getScrapCount().longValue() : 0L,
                post.getCommentCount() != null ? post.getCommentCount().longValue() : 0L,
                post.getLastCommentedAt(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                scrappedByMe,
                tags,
                List.of() // 첨부파일은 후순위
                );
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Author {
        private String displayName;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ConsultingTagSummary {
        private String code;
        private String name;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SkillTagSummary {
        private Long id;
        private String name;

        public static SkillTagSummary from(SkillTag tag) {
            return new SkillTagSummary(tag.getId(), tag.getName());
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AttachmentSummary {
        private Long id;
        private String fileUrl;
        private String fileName;
        private String fileType;
        private Long fileSize;
        private LocalDateTime createdAt;
    }
}

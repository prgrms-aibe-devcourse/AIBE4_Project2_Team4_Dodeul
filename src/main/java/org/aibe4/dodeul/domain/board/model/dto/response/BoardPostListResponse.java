package org.aibe4.dodeul.domain.board.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardPostListResponse {

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "상담 분야(카테고리)")
    private ConsultingTag consultingTag;

    @Schema(description = "제목", example = "스프링 JPA N+1 문제 해결이 궁금합니다")
    private String title;

    @Schema(description = "게시글 상태", example = "OPEN")
    private String postStatus;

    @Schema(description = "조회수", example = "10")
    private long viewCount;

    @Schema(description = "스크랩 수", example = "3")
    private long scrapCount;

    @Schema(description = "댓글 수", example = "2")
    private long commentCount;

    @Schema(description = "마지막 댓글 작성 시각", example = "2026-01-10T12:34:56")
    private LocalDateTime lastCommentedAt;

    @Schema(description = "작성 시각", example = "2026-01-10T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "내 스크랩 여부(로그인 시에만 의미 있음)", example = "false")
    private boolean scrappedByMe;

    @Schema(description = "스킬 태그 이름 목록", example = "[\"자소서\",\"포트폴리오\",\"백엔드\"]")
    private List<String> skillTags;
}

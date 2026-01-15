package org.aibe4.dodeul.domain.board.model.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "개별 댓글 정보")
public class BoardCommentItemResponse {

    @Schema(description = "댓글 ID", example = "10")
    private Long commentId;

    @Schema(description = "게시글 ID", example = "1")
    private Long postId;

    @Schema(description = "부모 댓글 ID (대댓글인 경우)", example = "5")
    private Long parentCommentId;

    @Schema(description = "최상위 댓글 ID", example = "5")
    private Long rootCommentId;

    @Schema(description = "댓글 깊이 (1: 루트, 2: 대댓글)", example = "1")
    private int depth;

    @Schema(description = "작성자 표시 이름 (닉네임)", example = "코딩고수")
    private String authorDisplayName;

    @Schema(description = "작성자 닉네임", example = "코딩고수")
    private String authorNickname;

    @Schema(description = "작성자 역할 태그 (작성자/멘토/멘티)", example = "멘토")
    private String authorRoleTag;

    @Schema(description = "작성자 프로필 이미지 URL", example = "https://...")
    private String authorProfileImageUrl;

    @Schema(description = "댓글 내용", example = "좋은 질문이네요.")
    private String content;

    @Schema(description = "댓글 상태 (PUBLISHED/DELETED)", example = "PUBLISHED")
    private String commentStatus;

    @Schema(description = "좋아요 수", example = "3")
    private int likeCount;

    @Schema(description = "채택 여부", example = "false")
    private boolean accepted;

    @Schema(description = "내가 좋아요를 눌렀는지 여부", example = "true")
    private boolean likedByMe;

    @Schema(description = "채택 가능 여부 (게시글 작성자만 true)", example = "true")
    private boolean canAccept;

    @Schema(description = "내가 작성한 댓글인지 여부", example = "false")
    private boolean mine;

    @Schema(description = "수정 가능 여부", example = "false")
    private boolean canEdit;

    @Schema(description = "삭제 가능 여부", example = "false")
    private boolean canDelete;

    @Schema(description = "답글 작성 가능 여부", example = "true")
    private boolean canReply;

    @Schema(description = "작성 시각", example = "2026-01-10T12:34:56")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시각", example = "2026-01-10T12:34:56")
    private LocalDateTime updatedAt;

    @Schema(description = "대댓글 목록")
    private List<BoardCommentItemResponse> children;
}

package org.aibe4.dodeul.domain.board.model.dto.comment;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class BoardCommentItemResponse {

    private Long commentId;
    private Long postId;
    private Long parentCommentId;
    private Long rootCommentId;
    private int depth;

    private String authorDisplayName;
    private String authorNickname;
    private String authorRoleTag;
    private String authorProfileImageUrl;  // 프로필 이미지 URL

    private String content;
    private String commentStatus;

    private int likeCount;
    private boolean accepted;
    private boolean likedByMe;

    // 채택 버튼 노출 여부(작성자 + OPEN + 루트댓글 + 내 댓글 아님 + 미채택 + 미삭제)
    private boolean canAccept;

    // CUD/답글 UI 제어용
    private boolean mine;
    private boolean canEdit;
    private boolean canDelete;
    private boolean canReply;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<BoardCommentItemResponse> children;
}

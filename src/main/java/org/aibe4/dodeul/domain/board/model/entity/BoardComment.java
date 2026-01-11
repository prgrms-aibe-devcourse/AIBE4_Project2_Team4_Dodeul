package org.aibe4.dodeul.domain.board.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.board.model.enums.CommentStatus;
import org.aibe4.dodeul.domain.common.model.entity.BaseEntity;

// import org.aibe4.dodeul.domain.member.model.entity.Member;

@Entity
@Table(name = "board_comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardComment extends BaseEntity {

    // TODO: Member Entity 완성 후 @ManyToOne 관계로 변경
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "member_id", nullable = false)
    // private Member member;

    @Column(name = "member_id", nullable = false)
    private Long memberId; // 임시 사용)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_post_id", nullable = false)
    private BoardPost boardPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private BoardComment parentComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_comment_id")
    private BoardComment rootComment;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "comment_status", nullable = false, length = 20)
    private CommentStatus commentStatus;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public BoardComment(
            Long memberId,
            BoardPost boardPost,
            BoardComment parentComment,
            BoardComment rootComment,
            String content) {
        this.memberId = memberId;
        this.boardPost = boardPost;
        this.parentComment = parentComment;
        this.rootComment = rootComment;
        this.content = content;
        this.commentStatus = CommentStatus.PUBLISHED;
        this.likeCount = 0;
    }

    public void update(String content) {
        validateNotDeleted();
        this.content = content;
    }

    public void delete() {
        validateNotDeleted();
        this.commentStatus = CommentStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    private void validateNotDeleted() {
        if (this.commentStatus == CommentStatus.DELETED) {
            throw new IllegalStateException("삭제된 댓글입니다");
        }
    }
}

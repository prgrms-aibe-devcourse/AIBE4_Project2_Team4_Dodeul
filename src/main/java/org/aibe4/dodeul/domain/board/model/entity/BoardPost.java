// src/main/java/org/aibe4/dodeul/domain/board/model/entity/BoardPost.java
package org.aibe4.dodeul.domain.board.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.board.model.enums.PostStatus;
import org.aibe4.dodeul.domain.common.model.entity.BaseEntity;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;

// import org.aibe4.dodeul.domain.member.model.entity.Member;

@Entity
@Table(name = "board_posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardPost extends BaseEntity {

    // TODO: Member Entity 완성 후 @ManyToOne 관계로 변경
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "member_id", nullable = false)
    // private Member member;

    @Column(name = "member_id", nullable = false)
    private Long memberId; // 임시 사용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accepted_comment_id")
    private BoardComment acceptedComment;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "board_consulting", nullable = false, length = 30)
    private ConsultingTag boardConsulting;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_status", nullable = false, length = 20)
    private PostStatus postStatus;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @Column(name = "scrap_count", nullable = false)
    private Integer scrapCount;

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount;

    @Column(name = "last_commented_at")
    private LocalDateTime lastCommentedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public BoardPost(Long memberId, String title, String content, ConsultingTag boardConsulting) {
        this.memberId = memberId;
        this.title = title;
        this.content = content;
        this.boardConsulting = boardConsulting;
        this.postStatus = PostStatus.OPEN;
        this.viewCount = 0;
        this.scrapCount = 0;
        this.commentCount = 0;
    }

    public void update(String title, String content, ConsultingTag boardConsulting) {
        validateNotDeleted();
        this.title = title;
        this.content = content;
        this.boardConsulting = boardConsulting;
    }

    public void delete() {
        validateNotDeleted();
        this.postStatus = PostStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    public void close() {
        validateNotDeleted();
        if (this.postStatus == PostStatus.CLOSED) {
            return;
        }
        this.postStatus = PostStatus.CLOSED;
    }

    public void acceptComment(BoardComment comment) {
        validateNotDeleted();
        if (this.acceptedComment != null) {
            throw new IllegalStateException("이미 채택된 댓글이 존재합니다");
        }
        this.acceptedComment = comment;
        this.close();
    }

    public void increaseViewCount() {
        validateNotDeleted();
        this.viewCount++;
    }

    public void increaseScrapCount() {
        this.scrapCount++;
    }

    public void decreaseScrapCount() {
        if (this.scrapCount > 0) {
            this.scrapCount--;
        }
    }

    public void increaseCommentCount() {
        this.commentCount++;
        this.lastCommentedAt = LocalDateTime.now();
    }

    public void decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    private void validateNotDeleted() {
        if (this.postStatus == PostStatus.DELETED) {
            throw new IllegalStateException("삭제된 게시글입니다");
        }
    }
}

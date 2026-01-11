package org.aibe4.dodeul.domain.board.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.BaseEntity;

// import org.aibe4.dodeul.domain.member.model.entity.Member;

@Entity
@Table(
        name = "board_comment_likes",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"board_comment_id", "member_id"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardCommentLike extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_comment_id", nullable = false)
    private BoardComment boardComment;

    // TODO: Member Entity 완성 후 @ManyToOne 관계로 변경
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "member_id", nullable = false)
    // private Member member;

    @Column(name = "member_id", nullable = false)
    private Long memberId; // 임시 사용

    @Builder
    public BoardCommentLike(BoardComment boardComment, Long memberId) {
        this.boardComment = boardComment;
        this.memberId = memberId;
    }
}

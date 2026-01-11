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
        name = "board_post_scraps",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"board_post_id", "member_id"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardPostScrap extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_post_id", nullable = false)
    private BoardPost boardPost;

    // TODO: Member Entity 완성 후 @ManyToOne 관계로 변경
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "member_id", nullable = false)
    // private Member member;

    @Column(name = "member_id", nullable = false)
    private Long memberId; // 임시 사용

    @Builder
    public BoardPostScrap(BoardPost boardPost, Long memberId) {
        this.boardPost = boardPost;
        this.memberId = memberId;
    }
}

package org.aibe4.dodeul.domain.board.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.BaseEntity;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;

@Entity
@Table(
    name = "board_post_tag_relations",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_board_post_tag_relations_post_id_tag_id",
            columnNames = {"post_id", "tag_id"})
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardPostTagRelation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private BoardPost boardPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private SkillTag skillTag;

    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;

    private BoardPostTagRelation(BoardPost boardPost, SkillTag skillTag) {
        this.boardPost = boardPost;
        this.skillTag = skillTag;
    }

    public static BoardPostTagRelation of(BoardPost boardPost, SkillTag skillTag) {
        return new BoardPostTagRelation(boardPost, skillTag);
    }
}

// src/main/java/org/aibe4/dodeul/domain/board/model/entity/BoardPostTagRelation.java
package org.aibe4.dodeul.domain.board.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.BaseEntity;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;

@Entity
@Table(
        name = "board_post_tag_relations",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"board_post_id", "skill_tag_id"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardPostTagRelation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_post_id", nullable = false)
    private BoardPost boardPost;

    // 기존 구조(skillTagId) 유지 + 공통 SkillTag 매핑 추가
    @Column(name = "skill_tag_id", nullable = false)
    private Long skillTagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "skill_tag_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_board_post_tag_relations_skill_tag"))
    private SkillTag skillTag;

    @Builder
    public BoardPostTagRelation(BoardPost boardPost, Long skillTagId) {
        this.boardPost = boardPost;
        this.skillTagId = skillTagId;
    }
}

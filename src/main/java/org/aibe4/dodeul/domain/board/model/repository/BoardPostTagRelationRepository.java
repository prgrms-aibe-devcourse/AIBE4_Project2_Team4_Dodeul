// src/main/java/org/aibe4/dodeul/domain/board/model/repository/BoardPostTagRelationRepository.java
package org.aibe4.dodeul.domain.board.model.repository;

import org.aibe4.dodeul.domain.board.model.entity.BoardPostTagRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardPostTagRelationRepository
    extends JpaRepository<BoardPostTagRelation, Long> {

    interface PostSkillTagRow {
        Long getPostId();

        String getTagName();
    }

    @Query(
        """
            select r.boardPost.id as postId, s.name as tagName
            from BoardPostTagRelation r
            join r.skillTag s
            where r.boardPost.id in :postIds
            """)
    List<PostSkillTagRow> findSkillTagNamesByPostIds(@Param("postIds") List<Long> postIds);
}

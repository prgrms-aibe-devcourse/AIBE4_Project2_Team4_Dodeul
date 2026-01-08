// src/main/java/org/aibe4/dodeul/domain/board/model/repository/BoardPostRepository.java
package org.aibe4.dodeul.domain.board.model.repository;

import java.util.Optional;
import org.aibe4.dodeul.domain.board.model.entity.BoardPost;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardPostRepository
        extends JpaRepository<BoardPost, Long>, BoardPostRepositoryCustom {

    @EntityGraph(attributePaths = {"boardPostTagRelations", "boardPostTagRelations.skillTag"})
    Optional<BoardPost> findDetailById(Long id);
}

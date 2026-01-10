package org.aibe4.dodeul.domain.board.model.repository;

import org.aibe4.dodeul.domain.board.model.entity.BoardPostTagRelation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardPostTagRelationRepository extends JpaRepository<BoardPostTagRelation, Long> {
}

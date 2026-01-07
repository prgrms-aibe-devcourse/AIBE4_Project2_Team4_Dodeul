package org.aibe4.dodeul.domain.board.repository;

import org.aibe4.dodeul.domain.board.model.entity.BoardPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardPostRepository
        extends JpaRepository<BoardPost, Long>, BoardPostRepositoryCustom {}

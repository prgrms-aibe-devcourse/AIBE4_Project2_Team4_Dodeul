// src/main/java/org/aibe4/dodeul/domain/board/model/repository/BoardPostScrapRepository.java
package org.aibe4.dodeul.domain.board.model.repository;

import org.aibe4.dodeul.domain.board.model.entity.BoardPostScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardPostScrapRepository extends JpaRepository<BoardPostScrap, Long> {

    boolean existsByBoardPostIdAndMemberId(Long boardPostId, Long memberId);
}

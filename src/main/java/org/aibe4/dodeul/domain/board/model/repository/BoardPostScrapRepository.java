// src/main/java/org/aibe4/dodeul/domain/board/model/repository/BoardPostScrapRepository.java
package org.aibe4.dodeul.domain.board.model.repository;

import org.aibe4.dodeul.domain.board.model.entity.BoardPostScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardPostScrapRepository extends JpaRepository<BoardPostScrap, Long> {

    boolean existsByBoardPostIdAndMemberId(Long boardPostId, Long memberId);

    long countByBoardPostId(Long boardPostId);

    void deleteByBoardPostIdAndMemberId(Long boardPostId, Long memberId);

    @Query(
        """
            select s
            from BoardPostScrap s
            join fetch s.boardPost p
            where s.memberId = :memberId
            order by s.id desc
            """)
    List<BoardPostScrap> findMyScrapsWithPost(@Param("memberId") Long memberId);
}

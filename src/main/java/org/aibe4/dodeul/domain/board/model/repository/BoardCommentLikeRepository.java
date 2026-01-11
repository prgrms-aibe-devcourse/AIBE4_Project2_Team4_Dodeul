package org.aibe4.dodeul.domain.board.model.repository;

import org.aibe4.dodeul.domain.board.model.entity.BoardCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardCommentLikeRepository extends JpaRepository<BoardCommentLike, Long> {

    Optional<BoardCommentLike> findByBoardCommentIdAndMemberId(Long boardCommentId, Long memberId);

    @Query(
        "select l.boardComment.id from BoardCommentLike l "
            + "where l.memberId = :memberId and l.boardComment.id in :commentIds")
    List<Long> findLikedCommentIds(Long memberId, List<Long> commentIds);
}

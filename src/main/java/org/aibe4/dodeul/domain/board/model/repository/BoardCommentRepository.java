package org.aibe4.dodeul.domain.board.model.repository;

import org.aibe4.dodeul.domain.board.model.entity.BoardComment;
import org.aibe4.dodeul.domain.board.model.enums.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {

    @Query(
        """
            select c
            from BoardComment c
            where c.boardPost.id = :postId
            order by
              case when c.rootComment is null then c.id else c.rootComment.id end asc,
              c.id asc
            """)
    List<BoardComment> findAllByPostId(@Param("postId") Long postId);

    // ✅ JPQL에서 연관관계 id만 업데이트는 불가 -> native로 root_comment_id를 채운다.
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        value = "update board_comments set root_comment_id = :rootCommentId where id = :commentId",
        nativeQuery = true)
    int updateRootCommentId(
        @Param("commentId") Long commentId, @Param("rootCommentId") Long rootCommentId);

    interface PostCommentCountRow {
        Long getPostId();

        Long getCnt();
    }

    @Query(
        """
            select c.boardPost.id as postId, count(c.id) as cnt
            from BoardComment c
            where c.boardPost.id in :postIds
              and c.commentStatus <> :deletedStatus
            group by c.boardPost.id
            """)
    List<PostCommentCountRow> findCommentCountsByPostIds(
        @Param("postIds") List<Long> postIds,
        @Param("deletedStatus") CommentStatus deletedStatus);
}

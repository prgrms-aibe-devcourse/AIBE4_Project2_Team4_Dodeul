// src/main/java/org/aibe4/dodeul/domain/board/model/repository/BoardPostRepository.java
package org.aibe4.dodeul.domain.board.model.repository;

import org.aibe4.dodeul.domain.board.model.entity.BoardPost;
import org.aibe4.dodeul.domain.board.model.enums.PostStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardPostRepository extends JpaRepository<BoardPost, Long>, BoardPostRepositoryCustom {

    // 상세 조회 시 태그 정보까지 한 번에 가져오기 (N+1 방지)
    @EntityGraph(attributePaths = {"boardPostTagRelations", "boardPostTagRelations.skillTag"})
    Optional<BoardPost> findDetailById(Long id);

    Optional<BoardPost> findByIdAndPostStatusNot(Long id, PostStatus postStatus);

    @Query(
        "select ac.id "
            + "from BoardPost p "
            + "left join p.acceptedComment ac "
            + "where p.id = :postId")
    Long findAcceptedCommentId(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        "update BoardPost p "
            + "set p.viewCount = p.viewCount + 1 "
            + "where p.id = :postId and p.postStatus <> :deletedStatus")
    int increaseViewCount(@Param("postId") Long postId, @Param("deletedStatus") PostStatus deletedStatus);
}

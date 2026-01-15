package org.aibe4.dodeul.domain.review.model.repository;

import org.aibe4.dodeul.domain.review.model.dto.NotReviewdDto;
import org.aibe4.dodeul.domain.review.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r.mentor.id, COUNT(r) FROM Review r " +
        "WHERE r.mentor.id IN :mentorIds AND r.isRecommended = true " +
        "GROUP BY r.mentor.id")
    List<Object[]> countRecommendedReviewsByMentorIds(@Param("mentorIds") List<Long> mentorIds);

    Page<Review> findAllByMentorId(Long mentorId, Pageable pageable);

    Page<Review> findAllByMenteeId(Long menteeId, Pageable pageable);

    boolean existsByMatchingId(Long matchingId);

    // 매칭의 상태가 INREVIEW인 매칭에서 작성되지 않은 리뷰들을 조회함
    @Query("SELECT new org.aibe4.dodeul.domain.review.model.dto.NotReviewdDto(" +
        "m.id, m.mentor.nickname, m.status) " +
        "FROM Matching m " +
        "LEFT JOIN Review r ON r.matching.id = m.id " +
        "WHERE m.mentee.id = :menteeId " +
        "AND m.status = org.aibe4.dodeul.domain.matching.model.enums.MatchingStatus.INREVIEW " +
        "AND r.id IS NULL " +
        "ORDER BY m.id DESC")
    List<NotReviewdDto> findNotReviewedMatches(@Param("menteeId") Long menteeId);
}

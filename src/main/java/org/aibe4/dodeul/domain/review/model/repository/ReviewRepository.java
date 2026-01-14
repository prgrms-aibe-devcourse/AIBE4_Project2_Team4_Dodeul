package org.aibe4.dodeul.domain.review.model.repository;

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
}

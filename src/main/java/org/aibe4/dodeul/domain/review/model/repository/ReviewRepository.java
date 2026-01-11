package org.aibe4.dodeul.domain.review.model.repository;

import org.aibe4.dodeul.domain.review.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}

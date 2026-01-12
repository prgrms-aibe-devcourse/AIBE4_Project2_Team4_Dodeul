package org.aibe4.dodeul.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.review.model.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Map<Long, Long> getRecommendedReviewCounts(List<Long> mentorIds) {
        if (mentorIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return reviewRepository.countRecommendedReviewsByMentorIds(mentorIds).stream()
            .collect(Collectors.toMap(
                obj -> (Long) obj[0],
                obj -> ((Number) obj[1]).longValue()
            ));
    }
}

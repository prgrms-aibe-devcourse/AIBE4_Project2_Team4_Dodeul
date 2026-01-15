package org.aibe4.dodeul.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.matching.model.entity.Matching;
import org.aibe4.dodeul.domain.matching.model.repository.MatchingRepository;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.service.MentorProfileService;
import org.aibe4.dodeul.domain.review.model.dto.NotReviewdDto;
import org.aibe4.dodeul.domain.review.model.dto.ReviewFormDataDto;
import org.aibe4.dodeul.domain.review.model.dto.ReviewRequest;
import org.aibe4.dodeul.domain.review.model.dto.ReviewResponse;
import org.aibe4.dodeul.domain.review.model.entity.Review;
import org.aibe4.dodeul.domain.review.model.repository.ReviewRepository;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final MatchingRepository matchingRepository;
    private final ReviewRepository reviewRepository;

    private final MentorProfileService mentorProfileService;

    public ReviewFormDataDto loadFormData(Long matchingId) {
        Matching matching = matchingRepository.findById(matchingId).orElseThrow(() -> new NoSuchElementException(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));

        Member mentor = matching.getMentor();
        Member mentee = matching.getMentee();

        return ReviewFormDataDto.of(mentor.getNickname(), mentee.getNickname());
    }

    @Transactional
    public void saveReview(Long matchingId, ReviewRequest request) {
        Matching matching = matchingRepository.findById(matchingId).orElseThrow(() -> new NoSuchElementException(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));

        Review review = Review.builder()
            .matching(matching)
            .content(request.getContent())
            .isRecommended(request.isRecommended())
            .build();

        matching.complete();

        if (request.isRecommended()) {
            mentorProfileService.increaseRecommendCount(matching.getMentor().getId());
        }

        reviewRepository.save(review);
    }

    public Page<ReviewResponse> getWrittenReviews(Long menteeId, Pageable pageable) {
        return reviewRepository.findAllByMenteeId(menteeId, pageable)
            .map(ReviewResponse::of);
    }

    public Page<ReviewResponse> getReceivedReviews(Long mentorId, Pageable pageable) {
        return reviewRepository.findAllByMentorId(mentorId, pageable)
            .map(ReviewResponse::of);
    }

    public boolean hasReview(Long matchingId) {
        return reviewRepository.existsByMatchingId(matchingId);
    }

    public List<NotReviewdDto> notReviewdDtoList(Long menteeId) {
        return reviewRepository.findNotReviewedMatches(menteeId);
    }
}

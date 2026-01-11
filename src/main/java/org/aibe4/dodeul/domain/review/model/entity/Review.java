package org.aibe4.dodeul.domain.review.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.BaseEntity;
import org.aibe4.dodeul.domain.matching.model.entity.Matching;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.review.model.entity.enums.ReviewStatus;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Member mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id", nullable = false)
    private Member mentee;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matching_id", nullable = false, unique = true)
    private Matching matching;

    @Column(name = "content", length = 200)
    private String content;

    @Column(name = "is_recommended", nullable = false)
    private boolean isRecommended;

    @Column(name = "review_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReviewStatus reviewStatus = ReviewStatus.PUBLISHED;

    @Builder
    public Review(Member mentor, Member mentee, Matching matching, String content, boolean isRecommended) {
        this.mentor = mentor;
        this.mentee = mentee;
        this.matching = matching;
        this.content = content;
        this.isRecommended = isRecommended;
    }

}

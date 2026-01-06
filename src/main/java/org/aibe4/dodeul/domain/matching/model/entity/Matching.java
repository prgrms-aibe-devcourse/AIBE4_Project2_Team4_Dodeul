package org.aibe4.dodeul.domain.matching.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.BaseEntity;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;
import org.aibe4.dodeul.domain.matching.model.enums.MatchingStatus;
import org.aibe4.dodeul.domain.member.model.entity.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "matchings")
public class Matching extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id", nullable = false)
    private Member mentee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Member mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private ConsultingApplication application;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MatchingStatus status;

    @Builder
    public Matching(Member mentee, Member mentor, ConsultingApplication application) {
        this.mentee = mentee;
        this.mentor = mentor;
        this.application = application;
        this.status = MatchingStatus.WAITING;
    }

    public void accept() {
        if (this.status != MatchingStatus.WAITING) {
            throw new IllegalStateException("매칭 대기 상태에서만 수락할 수 있습니다.");
        }
        this.status = MatchingStatus.MATCHED;
    }

    public void reject() {
        if (this.status != MatchingStatus.WAITING) {
            throw new IllegalStateException("매칭 대기 상태에서만 거절할 수 있습니다.");
        }
        this.status = MatchingStatus.REJECTED;
    }

    public void cancel() {
        if (this.status != MatchingStatus.WAITING) {
            throw new IllegalStateException("이미 진행 중이거나 종료된 매칭은 취소할 수 없습니다.");
        }
        this.status = MatchingStatus.CANCELED;
    }

    public void expire() {
        if (this.status != MatchingStatus.WAITING) {
            // 스케줄러 실행 시점의 동시성 문제를 고려하여, 이미 처리된 건은 예외 처리 없이 무시
            return;
        }
        this.status = MatchingStatus.TIMEOUT;
    }

    public void finishConsulting() {
        if (this.status != MatchingStatus.MATCHED) {
            throw new IllegalStateException("진행 중인 상담만 종료할 수 있습니다.");
        }
        this.status = MatchingStatus.INREVIEW;
    }

    public void complete() {
        if (this.status != MatchingStatus.INREVIEW) {
            throw new IllegalStateException("리뷰 작성 대기 상태가 아닙니다.");
        }
        this.status = MatchingStatus.COMPLETED;
    }
}

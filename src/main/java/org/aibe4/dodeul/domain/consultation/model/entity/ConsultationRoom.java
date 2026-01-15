package org.aibe4.dodeul.domain.consultation.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.BaseEntity;
import org.aibe4.dodeul.domain.consultation.model.enums.ConsultationRoomStatus;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;
import org.aibe4.dodeul.domain.matching.model.entity.Matching;

import java.time.LocalDateTime;

@Entity
@Table(name = "consultation_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsultationRoom extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConsultationRoomStatus status = ConsultationRoomStatus.OPEN;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matching_id", nullable = false, unique = true)
    private Matching matching;

    @Builder
    public ConsultationRoom(Matching matching) {
        this.matching = matching;
    }

    public static ConsultationRoom createRoom(Matching matching) {
        return ConsultationRoom.builder()
            .matching(matching)
            .build();
    }

    public void changeStatusToClose() {
        if (this.status == ConsultationRoomStatus.CLOSED) {
            throw new IllegalStateException("이미 종료된 상담방입니다.");
        }
        this.status = ConsultationRoomStatus.CLOSED;
        this.closedAt = LocalDateTime.now();
    }

    public Matching getValidatedMatching() {
        if (this.matching == null) {
            throw new IllegalStateException("상담방에 연결된 매칭 정보를 찾을 수 없습니다.");
        }

        return this.matching;
    }

    public ConsultingApplication getValidatedApplication() {
        Matching validMatching = getValidatedMatching();
        ConsultingApplication application = validMatching.getApplication();

        if (application == null) {
            throw new IllegalStateException("매칭에 연결된 상담 신청서를 찾을 수 없습니다.");
        }

        return application;
    }
}

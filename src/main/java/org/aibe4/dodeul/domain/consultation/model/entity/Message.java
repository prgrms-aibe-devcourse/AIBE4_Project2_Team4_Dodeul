package org.aibe4.dodeul.domain.consultation.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.BaseEntity;
import org.aibe4.dodeul.domain.consultation.model.enums.MessageType;

@Entity
@Table(name = "messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MessageType messageType;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_room_id", nullable = false)
    private ConsultationRoom consultationRoom;

    //    @ManyToOne(fetch = FetchType.LAZY)
    //    @JoinColumn(name = "member_id", nullable = false)
    //    private Member sender;

    //    @Builder
    //    public Message(MessageType messageType, String content, ConsultationRoom consultationRoom,
    //                   Member sender) {
    //        this.messageType = messageType;
    //        this.content = content;
    //        this.consultationRoom = consultationRoom;
    //        this.sender = sender;
    //    }
}

package org.aibe4.dodeul.domain.consultation.model.dto;

import lombok.Builder;
import lombok.Getter;
import org.aibe4.dodeul.domain.consultation.model.entity.Message;
import org.aibe4.dodeul.domain.consultation.model.enums.MessageType;

@Getter
@Builder
public class MessageDto {

    private Long senderId;
    private String senderNickname;
    private MessageType type;
    private String content;

    public static MessageDto of(Message message) {
        return MessageDto.builder()
                .senderId(message.getSender().getId())
                .senderNickname(message.getSender().getNickname())
                .type(message.getMessageType())
                .content(message.getContent())
                .build();
    }
}

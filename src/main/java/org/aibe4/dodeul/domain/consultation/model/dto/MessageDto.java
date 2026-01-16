package org.aibe4.dodeul.domain.consultation.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.aibe4.dodeul.domain.consultation.model.entity.Message;
import org.aibe4.dodeul.domain.consultation.model.enums.MessageType;

@Getter
@Builder
@Schema(description = "상담 채팅 메시지 응답 DTO")
public class MessageDto {

    @Schema(description = "메시지 ID", example = "101")
    private Long id;

    @Schema(description = "보낸 사람 ID (회원 PK)", example = "15")
    private Long senderId;

    @Schema(description = "보낸 사람 닉네임", example = "김멘토")
    private String senderNickname;

    @Schema(description = "메시지 타입 (TEXT, IMAGE, FILE, SYSTEM)", example = "TEXT")
    private MessageType type;

    @Schema(description = "메시지 내용 (텍스트 내용 또는 파일/이미지 URL)", example = "안녕하세요. 포트폴리오 첨부합니다.")
    private String content;

    @Schema(description = "원본 파일명 (파일/이미지 메시지일 경우에만 존재)", example = "portfolio_v1.pdf")
    private String fileName;

    public static MessageDto of(Message message) {
        return MessageDto.builder()
            .id(message.getId())
            .senderId(message.getSender().getId())
            .senderNickname(message.getSender().getNickname())
            .type(message.getMessageType())
            .content(message.getContent())
            .build();
    }

    public static MessageDto of(Message message, String fileName) {
        return MessageDto.builder()
            .id(message.getId())
            .senderId(message.getSender().getId())
            .senderNickname(message.getSender().getNickname())
            .type(message.getMessageType())
            .content(message.getContent())
            .fileName(fileName) // 전달받은 파일명 매핑
            .build();
    }
}

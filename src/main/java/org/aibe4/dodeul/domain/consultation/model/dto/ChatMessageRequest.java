package org.aibe4.dodeul.domain.consultation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.consultation.model.enums.MessageType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
    private Long roomId;
    private String content;
    private MessageType messageType;

    // 파일
    private String fileName;
    private Long fileSize;
    private String contentType;
}

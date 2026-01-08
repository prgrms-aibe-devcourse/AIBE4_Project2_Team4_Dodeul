package org.aibe4.dodeul.domain.consultation.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consultation.model.dto.ChatMessageRequest;
import org.aibe4.dodeul.domain.consultation.model.dto.MessageDto;
import org.aibe4.dodeul.domain.consultation.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/send")
    public void sendMessage(@Payload ChatMessageRequest request) {
        MessageDto savedMessage = chatService.saveMessage(request);
        // 구독 중인 클라이언트들에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/room/" + request.getRoomId(), savedMessage);
    }
}

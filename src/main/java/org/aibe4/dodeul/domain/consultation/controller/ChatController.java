package org.aibe4.dodeul.domain.consultation.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consultation.model.dto.ChatMessageRequest;
import org.aibe4.dodeul.domain.consultation.model.dto.MessageDto;
import org.aibe4.dodeul.domain.consultation.service.ChatService;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/send")
    @PreAuthorize("@consultationGuard.isParticipantMember(#request.roomId, #userDetails.memberId)")
    public void sendMessage(@Payload ChatMessageRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        MessageDto savedMessage = chatService.saveMessage(request, userDetails.getMemberId());
        // 구독 중인 클라이언트들에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/room/" + request.getRoomId(), savedMessage);
    }
}

package org.aibe4.dodeul.domain.consultation.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consultation.model.dto.ChatMessageRequest;
import org.aibe4.dodeul.domain.consultation.model.dto.MessageDto;
import org.aibe4.dodeul.domain.consultation.security.ConsultationGuard;
import org.aibe4.dodeul.domain.consultation.service.ChatService;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ConsultationGuard consultationGuard;

    @MessageMapping("/chat/send")
    public void sendMessage(@Payload ChatMessageRequest request, Principal principal) {

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long memberId = userDetails.getMemberId();

        if (!consultationGuard.isParticipantMember(request.getRoomId(), memberId)) {
            throw new AccessDeniedException(ErrorCode.ACCESS_DENIED.getMessage());
        }

        MessageDto savedMessage = chatService.saveMessage(request, memberId);

        messagingTemplate.convertAndSend("/topic/room/" + request.getRoomId(), savedMessage);
    }
}

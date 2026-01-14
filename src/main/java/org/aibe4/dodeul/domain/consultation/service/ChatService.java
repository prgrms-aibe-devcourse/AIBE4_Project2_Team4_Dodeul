package org.aibe4.dodeul.domain.consultation.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consultation.model.dto.ChatMessageRequest;
import org.aibe4.dodeul.domain.consultation.model.dto.MessageDto;
import org.aibe4.dodeul.domain.consultation.model.entity.ConsultationRoom;
import org.aibe4.dodeul.domain.consultation.model.entity.Message;
import org.aibe4.dodeul.domain.consultation.model.enums.MessageType;
import org.aibe4.dodeul.domain.consultation.model.repository.ConsultationRoomRepository;
import org.aibe4.dodeul.domain.consultation.model.repository.MessageRepository;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.repository.MemberRepository;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

    private final MemberRepository memberRepository;
    private final ConsultationRoomRepository consultationRoomRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final int INITIAL_MESSAGE_SIZE = 20;

    @Transactional
    public MessageDto saveMessage(ChatMessageRequest request) {
        ConsultationRoom room = consultationRoomRepository.findById(request.getRoomId()).orElseThrow(() -> new NoSuchElementException(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));
        Member sender = memberRepository.findById(request.getSenderId()).orElseThrow(() -> new NoSuchElementException(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));

        Message message = Message.builder()
            .consultationRoom(room)
            .sender(sender)
            .content(request.getContent())
            .messageType(MessageType.TEXT) // 기본 타입 텍스트
            .build();

        messageRepository.save(message);

        return MessageDto.of(message);
    }

    public Slice<MessageDto> getMoreMessages(Long roomId, Long lastMessageId, int size) {
        PageRequest pageRequest = PageRequest.of(0, size);

        return messageRepository.findOldMessagesByCursor(roomId, lastMessageId, pageRequest)
            .map(MessageDto::of);
    }

    public List<MessageDto> getInitialMessageList(Long roomId) {
        PageRequest pageRequest = PageRequest.of(0, INITIAL_MESSAGE_SIZE);

        return messageRepository.findLatestMessages(roomId, pageRequest)
            .getContent()
            .stream()
            .map(MessageDto::of)
            .sorted(Comparator.comparing(MessageDto::getId))
            .toList();
    }

    @Transactional
    public void sendSystemMessage(Long roomId, Long currentMemberId, String content) {
        Member member = memberRepository.findById(currentMemberId).orElseThrow(() -> new NoSuchElementException(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));
        ConsultationRoom room = consultationRoomRepository.findById(roomId).orElseThrow(() -> new NoSuchElementException(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));

        Message systemMessage = Message.builder()
            .consultationRoom(room)
            .sender(member)
            .content(content)
            .messageType(MessageType.SYSTEM)
            .build();

        messageRepository.save(systemMessage);

        messagingTemplate.convertAndSend("/topic/room/" + roomId, MessageDto.of(systemMessage));
    }
}

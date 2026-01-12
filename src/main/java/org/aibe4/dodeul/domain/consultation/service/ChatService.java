package org.aibe4.dodeul.domain.consultation.service;

import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

    private final MemberRepository memberRepository;
    private final ConsultationRoomRepository consultationRoomRepository;
    private final MessageRepository messageRepository;

    private static final int INITIAL_MESSAGE_SIZE = 20;

    @Transactional
    public MessageDto saveMessage(ChatMessageRequest request) {
        ConsultationRoom room = consultationRoomRepository.findById(request.getRoomId()).orElseThrow(() -> new EntityNotFoundException("해당 ID의 상담방을 찾을 수 없습니다." + request.getRoomId()));
        Member sender = memberRepository.findById(request.getSenderId()).orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + request.getSenderId()));

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
}

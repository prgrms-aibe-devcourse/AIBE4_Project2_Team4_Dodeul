package org.aibe4.dodeul.domain.consultation.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.CommonFile;
import org.aibe4.dodeul.domain.common.model.enums.FileDomain;
import org.aibe4.dodeul.domain.common.repository.CommonFileRepository;
import org.aibe4.dodeul.domain.consultation.model.dto.ChatMessageRequest;
import org.aibe4.dodeul.domain.consultation.model.dto.MessageDto;
import org.aibe4.dodeul.domain.consultation.model.entity.ConsultationRoom;
import org.aibe4.dodeul.domain.consultation.model.entity.Message;
import org.aibe4.dodeul.domain.consultation.model.enums.MessageType;
import org.aibe4.dodeul.domain.consultation.model.repository.ConsultationRoomRepository;
import org.aibe4.dodeul.domain.consultation.model.repository.MessageRepository;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.repository.MemberRepository;
import org.aibe4.dodeul.global.exception.BusinessException;
import org.aibe4.dodeul.global.file.model.dto.response.FileUploadResponse;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

    private final MemberRepository memberRepository;
    private final ConsultationRoomRepository consultationRoomRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final int INITIAL_MESSAGE_SIZE = 20;
    private final CommonFileRepository commonFileRepository;

    @Transactional
    public MessageDto saveMessage(ChatMessageRequest request, Long currentMemberId) {
        ConsultationRoom room = consultationRoomRepository.findById(request.getRoomId()).orElseThrow(() -> new NoSuchElementException(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));
        Member sender = memberRepository.findById(currentMemberId).orElseThrow(() -> new NoSuchElementException(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));

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
        Slice<Message> messageSlice = messageRepository.findOldMessagesByCursor(roomId, lastMessageId, pageRequest);

        // 파일명 매핑 처리 후 반환
        return messageSlice.map(m -> mapToDtoWithFileName(m, getFileNameMap(List.of(m.getId()))));
    }

    /**
     * 공통 로직: 메시지 ID 목록으로 파일명 맵 조회
     */
    private Map<Long, String> getFileNameMap(List<Long> messageIds) {
        if (messageIds.isEmpty()) return Map.of();

        return commonFileRepository.findAllByMessageIdInAndDomain(messageIds, FileDomain.CHAT_MESSAGE)
            .stream()
            .collect(Collectors.toMap(CommonFile::getMessageId, CommonFile::getOriginFileName));
    }

    /**
     * 공통 로직: 파일명이 존재하면 포함하여 DTO 변환
     */
    private MessageDto mapToDtoWithFileName(Message m, Map<Long, String> fileNameMap) {
        String fileName = fileNameMap.get(m.getId());
        return (fileName != null) ? MessageDto.of(m, fileName) : MessageDto.of(m);
    }

    /**
     * 초기 메시지 목록 조회 (방 진입 시)
     */
    public List<MessageDto> getInitialMessageList(Long roomId) {
        PageRequest pageRequest = PageRequest.of(0, INITIAL_MESSAGE_SIZE);
        List<Message> messages = messageRepository.findLatestMessages(roomId, pageRequest).getContent();

        // 1. 파일/이미지 타입 메시지 ID 추출
        List<Long> fileMessageIds = messages.stream()
            .filter(m -> m.getMessageType() == MessageType.FILE || m.getMessageType() == MessageType.IMAGE)
            .map(Message::getId)
            .toList();

        // 2. 파일명 맵 생성
        Map<Long, String> fileNameMap = getFileNameMap(fileMessageIds);

        // 3. DTO 변환 시 파일명 매핑
        return messages.stream()
            .map(m -> mapToDtoWithFileName(m, fileNameMap))
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

    @Transactional
    public MessageDto saveFileMessage(Long roomId, Long senderId, FileUploadResponse fileInfo) {
        ConsultationRoom room = consultationRoomRepository.findById(roomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        Member sender = memberRepository.findById(senderId).orElseThrow(() -> new NoSuchElementException(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));

        // 이미지 여부 판단
        MessageType type = fileInfo.getContentType().startsWith("image")
            ? MessageType.IMAGE : MessageType.FILE;

        Message message = Message.builder()
            .consultationRoom(room)
            .sender(sender)
            .content(fileInfo.getFileUrl()) // Supabase URL 저장
            .messageType(type)
            .build();

        Message savedMessage = messageRepository.save(message);

        CommonFile commonFile = CommonFile.ofChatMessage(
            savedMessage.getId(),
            fileInfo.getFileUrl(),
            fileInfo.getOriginFileName(),
            fileInfo.getContentType(),
            fileInfo.getFileSize()
        );
        commonFileRepository.save(commonFile);

        MessageDto dto = MessageDto.of(savedMessage, fileInfo.getOriginFileName());

        messagingTemplate.convertAndSend("/topic/room/" + roomId, dto);

        return dto;
    }

    public List<MessageDto> getChatFiles(Long roomId) {
        // 1. 해당 방의 파일/이미지 메시지 조회
        List<Message> messages = messageRepository.findFileMessagesByRoomId(roomId);

        if (messages.isEmpty()) return List.of();

        // 2. 메시지 ID 목록 추출
        List<Long> messageIds = messages.stream().map(Message::getId).toList();

        // 3. CommonFile 테이블에서 실제 파일 정보(파일명 등) 조회
        List<CommonFile> commonFiles = commonFileRepository.findAllByMessageIdInAndDomain(
            messageIds,
            FileDomain.CHAT_MESSAGE
        );

        // 4. 빠른 조회를 위해 messageId를 키로 하는 Map 생성
        Map<Long, CommonFile> fileMap = commonFiles.stream()
            .collect(Collectors.toMap(CommonFile::getMessageId, f -> f));

        // 5. DTO 변환 과정에서 파일명 주입
        return messages.stream()
            .map(m -> {
                CommonFile f = fileMap.get(m.getId());
                // 파일 정보가 DB에 있으면 원본 이름을, 없으면 기본값 설정
                String displayName = (f != null) ? f.getOriginFileName() : "첨부파일";

                // 수정된 MessageDto.of 호출 (파일명 포함)
                return MessageDto.of(m, displayName);
            })
            .toList();
    }
}

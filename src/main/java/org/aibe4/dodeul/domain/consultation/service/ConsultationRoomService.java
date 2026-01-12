package org.aibe4.dodeul.domain.consultation.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consultation.model.dto.ConsultationRoomDto;
import org.aibe4.dodeul.domain.consultation.model.dto.MessageDto;
import org.aibe4.dodeul.domain.consultation.model.entity.ConsultationRoom;
import org.aibe4.dodeul.domain.consultation.model.repository.ConsultationRoomRepository;
import org.aibe4.dodeul.domain.matching.model.entity.Matching;
import org.aibe4.dodeul.domain.matching.model.repository.MatchingRepository;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConsultationRoomService {

    private final MatchingRepository matchingRepository;
    private final ConsultationRoomRepository consultationRoomRepository;
    private final ChatService chatService;

    public ConsultationRoomDto loadRoomInfo(Long roomId, Long currentMemberId) {
        ConsultationRoom room = consultationRoomRepository.findById(roomId).orElseThrow(() -> new NoSuchElementException(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));

        List<MessageDto> messageDtoList = chatService.getInitialMessageList(roomId);

        return ConsultationRoomDto.of(room, messageDtoList, currentMemberId);
    }

    @Transactional
    public Long getOrCreateRoom(Long matchingId) {
        Matching matching = matchingRepository.findById(matchingId).orElseThrow(() -> new NoSuchElementException(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));

        return consultationRoomRepository.findByMatching(matching)
            .map(ConsultationRoom::getId)
            .orElseGet(() -> createAndSaveRoom(matching));
    }

    private Long createAndSaveRoom(Matching matching) {
        ConsultationRoom newRoom = ConsultationRoom.createRoom(matching);

        return consultationRoomRepository.save(newRoom).getId();
    }

    @Transactional
    public void closeRoom(Long roomId, Long currentMemberId) {
        ConsultationRoom room = consultationRoomRepository.findById(roomId).orElseThrow(() -> new NoSuchElementException(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));

        room.changeStatusToClose();

        chatService.sendSystemMessage(roomId, currentMemberId, "상담이 종료되었습니다.");
    }
}

package org.aibe4.dodeul.domain.consultation.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consultation.model.dto.ConsultationRoomDto;
import org.aibe4.dodeul.domain.consultation.model.dto.MessageDto;
import org.aibe4.dodeul.domain.consultation.model.entity.ConsultationRoom;
import org.aibe4.dodeul.domain.consultation.model.repository.ConsultationRoomRepository;
import org.aibe4.dodeul.domain.matching.model.entity.Matching;
import org.aibe4.dodeul.domain.matching.model.repository.MatchingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConsultationRoomService {

    private final MatchingRepository matchingRepository;
    private final ConsultationRoomRepository consultationRoomRepository;
    private final ChatService chatService;

    public ConsultationRoomDto loadRoomInfo(Long roomId, Long currentMemberId) {
        ConsultationRoom room = consultationRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("해당 ID의 상담방을 찾을 수 없습니다." + roomId));

        List<MessageDto> messageDtoList = chatService.getInitialMessageList(roomId);

        return ConsultationRoomDto.of(room, messageDtoList, currentMemberId);
    }

    @Transactional
    public Long getOrCreateRoom(Long matchingId) {
        Matching matching = matchingRepository.findById(matchingId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매칭입니다."));

        return consultationRoomRepository.findByMatching(matching)
            .map(ConsultationRoom::getId)
            .orElseGet(() -> createAndSaveRoom(matching));
    }

    private Long createAndSaveRoom(Matching matching) {
        ConsultationRoom newRoom = ConsultationRoom.createRoom(matching);

        return consultationRoomRepository.save(newRoom).getId();
    }
}

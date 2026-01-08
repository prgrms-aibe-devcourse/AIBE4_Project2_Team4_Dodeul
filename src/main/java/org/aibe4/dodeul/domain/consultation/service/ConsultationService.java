package org.aibe4.dodeul.domain.consultation.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consultation.model.dto.ConsultationRoomDto;
import org.aibe4.dodeul.domain.consultation.model.dto.MessageDto;
import org.aibe4.dodeul.domain.consultation.model.entity.ConsultationRoom;
import org.aibe4.dodeul.domain.consultation.model.repository.ConsultationRoomRepository;
import org.aibe4.dodeul.domain.consultation.model.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConsultationService {

    private final ConsultationRoomRepository consultationRoomRepository;
    private final MessageRepository messageRepository;

    public ConsultationRoomDto getRoomWithApplication(Long roomId, Long currentMemberId) {
        ConsultationRoom room =
                consultationRoomRepository
                        .findById(roomId)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "해당 ID의 상담방을 찾을 수 없습니다." + roomId));

        List<MessageDto> messageDtoList = getMessageDtoList(roomId, currentMemberId);

        return ConsultationRoomDto.of(room, messageDtoList, currentMemberId);
    }

    private List<MessageDto> getMessageDtoList(Long roomId, Long currentMemberId) {
        return messageRepository.findByConsultationRoomIdOrderByCreatedAtAsc(roomId).stream()
                .map(MessageDto::of)
                .toList();
    }
}

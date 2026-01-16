package org.aibe4.dodeul.domain.consultation.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consultation.model.dto.ConsultationRoomDto;
import org.aibe4.dodeul.domain.consultation.model.dto.MessageDto;
import org.aibe4.dodeul.domain.consultation.model.entity.ConsultationRoom;
import org.aibe4.dodeul.domain.consultation.model.repository.ConsultationRoomRepository;
import org.aibe4.dodeul.domain.matching.model.entity.Matching;
import org.aibe4.dodeul.domain.matching.model.repository.MatchingRepository;
import org.aibe4.dodeul.domain.member.service.MentorProfileService;
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
    private final MentorProfileService mentorProfileService;

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
        ConsultationRoom savedRoom = consultationRoomRepository.save(newRoom);

        Long mentorId = matching.getMentor().getId();

        chatService.sendSystemMessage(savedRoom.getId(), mentorId, "\"\uD83D\uDCE2 멘토링 시작! 궁금한 점을 자유롭게 질문하고 답변해 주세요.\"️");

        return savedRoom.getId();
    }

    @Transactional
    public void closeRoom(Long roomId, Long currentMemberId) {
        ConsultationRoom room = consultationRoomRepository.findById(roomId).orElseThrow(() -> new NoSuchElementException(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));

        room.getMatching().finishConsulting();

        room.changeStatusToClose();

        mentorProfileService.increaseCompletedMatchingCount(room.getMatching().getMentor().getId());

        chatService.sendSystemMessage(roomId, currentMemberId, "\"\uD83C\uDFC1 상담이 종료되었습니다. 두 분 모두 고생 많으셨습니다! 유익한 시간 되셨기를 바랍니다.\"");
    }
}

package org.aibe4.dodeul.domain.consultation.security;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consultation.model.entity.ConsultationRoom;
import org.aibe4.dodeul.domain.consultation.model.enums.ConsultationRoomStatus;
import org.aibe4.dodeul.domain.consultation.model.repository.ConsultationRoomRepository;
import org.aibe4.dodeul.domain.matching.model.entity.Matching;
import org.aibe4.dodeul.domain.matching.model.repository.MatchingRepository;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Component("consultationGuard")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConsultationGuard {

    private final ConsultationRoomRepository consultationRoomRepository;
    private final MatchingRepository matchingRepository;

    public boolean isParticipantMember(Long roomId, Long memberId) {
        ConsultationRoom room = consultationRoomRepository.findById(roomId)
            .orElseThrow(() -> new NoSuchElementException(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));

        Matching matching = room.getValidatedMatching();

        if (!isMatchingParticipant(matching, memberId)) {
            throw new AccessDeniedException(ErrorCode.ACCESS_DENIED.getMessage());
        }

        return true;
    }

    public boolean isCorrectMatchedMember(Long matchingId, Long memberId) {
        if (isInvalidInput(matchingId, memberId)) {
            return false;
        }

        return matchingRepository.isMemberParticipantOfMatching(matchingId, memberId);
    }

    public boolean isCorrectMatchedMemberAndRoomClosed(Long matchingId, Long memberId) {
        if (isInvalidInput(matchingId, memberId)) {
            return false;
        }

        Matching matching = findMatchingById(matchingId);
        ConsultationRoom consultationRoom = consultationRoomRepository.findByMatching(matching)
            .orElseThrow(() -> new NoSuchElementException(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));

        return isMatchingParticipant(matching, memberId) &&
            consultationRoom.getStatus() == ConsultationRoomStatus.CLOSED;
    }

    public boolean isMenteeOfMatching(Long matchingId, Long memberId) {
        if (isInvalidInput(matchingId, memberId)) {
            return false;
        }

        return matchingRepository.findById(matchingId)
            .map(matching -> matching.getMentee().getId().equals(memberId))
            .orElse(false);
    }

    private Matching findMatchingById(Long matchingId) {
        return matchingRepository.findById(matchingId)
            .orElseThrow(() -> new NoSuchElementException(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));
    }

    private boolean isMatchingParticipant(Matching matching, Long memberId) {
        Long mentorId = matching.getMentor().getId();
        Long menteeId = matching.getMentee().getId();
        return memberId.equals(mentorId) || memberId.equals(menteeId);
    }

    private boolean isInvalidInput(Long matchingId, Long memberId) {
        return matchingId == null || memberId == null;
    }
}

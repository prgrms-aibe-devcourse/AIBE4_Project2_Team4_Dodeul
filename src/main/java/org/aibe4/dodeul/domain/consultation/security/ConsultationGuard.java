package org.aibe4.dodeul.domain.consultation.security;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consultation.model.entity.ConsultationRoom;
import org.aibe4.dodeul.domain.consultation.model.repository.ConsultationRoomRepository;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component("consultationGuard")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConsultationGuard {

    private final ConsultationRoomRepository consultationRoomRepository;

    public boolean check(Long roomId, Long memberId) {
        ConsultationRoom consultationRoom = consultationRoomRepository.findById(roomId).orElseThrow(() -> new AccessDeniedException(ErrorCode.CONSULTATION_ROOM_ACCESS_DENIED.getMessage()));

        Member mentor = consultationRoom.getMatching().getMentor();
        Member mentee = consultationRoom.getMatching().getMentee();

        if (!Objects.equals(memberId, mentor.getId()) && !Objects.equals(memberId, mentee.getId())) {
            throw new AccessDeniedException(ErrorCode.CONSULTATION_ROOM_ACCESS_DENIED.getMessage());
        }

        return true;
    }
}

package org.aibe4.dodeul.domain.consultation.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consultation.service.ConsultationRoomService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consultations/room")
@RequiredArgsConstructor
public class ConsultationRoomApiController {

    private final ConsultationRoomService consultationRoomService;

    @PostMapping("/{roomId}/close")
    @PreAuthorize("@consultationGuard.isParticipantMember(#roomId, #userDetails.memberId)")
    public CommonResponse<Void> closeRoom(@PathVariable Long roomId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        consultationRoomService.closeRoom(roomId, userDetails.getMemberId());

        return CommonResponse.success(SuccessCode.SUCCESS, null);
    }
}

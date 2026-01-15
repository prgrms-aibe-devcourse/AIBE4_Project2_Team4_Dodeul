package org.aibe4.dodeul.domain.consultation.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consultation.model.dto.MessageDto;
import org.aibe4.dodeul.domain.consultation.service.ChatService;
import org.aibe4.dodeul.domain.consultation.service.ConsultationRoomService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultations/room")
@RequiredArgsConstructor
public class ConsultationRoomApiController {

    private final ConsultationRoomService consultationRoomService;
    private final ChatService chatService;

    @PostMapping("/{roomId}/close")
    @PreAuthorize("@consultationGuard.isParticipantMember(#roomId, #userDetails.memberId)")
    public CommonResponse<Void> closeRoom(@PathVariable Long roomId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        consultationRoomService.closeRoom(roomId, userDetails.getMemberId());

        return CommonResponse.success(SuccessCode.SUCCESS, null);
    }

    @GetMapping("/{roomId}/files")
    public CommonResponse<List<MessageDto>> getChatFiles(@PathVariable Long roomId) {
        // 메시지 중 type이 IMAGE 또는 FILE인 것들만 최신순으로 조회
        List<MessageDto> files = chatService.getChatFiles(roomId);
        return CommonResponse.success(SuccessCode.SUCCESS, files);
    }

}

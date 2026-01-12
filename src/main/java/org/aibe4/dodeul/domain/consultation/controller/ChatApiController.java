package org.aibe4.dodeul.domain.consultation.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consultation.model.dto.MessageDto;
import org.aibe4.dodeul.domain.consultation.service.ChatService;
import org.aibe4.dodeul.domain.consultation.service.ConsultationRoomService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consultations/room")
@RequiredArgsConstructor
public class ChatApiController {

    private final ConsultationRoomService consultationRoomService;
    private final ChatService chatService;

    @GetMapping("/{roomId}/messages")
    @PreAuthorize("@consultationGuard.isParticipantMember(#roomId, #userDetails.memberId)")
    public CommonResponse<Slice<MessageDto>> getMoreMessages(@PathVariable Long roomId,
                                                             @RequestParam(required = false) Long lastMessageId,
                                                             @RequestParam(defaultValue = "20") int size,
                                                             @AuthenticationPrincipal CustomUserDetails userDetails) {

        Slice<MessageDto> messages = chatService.getMoreMessages(roomId, lastMessageId, size);
        return CommonResponse.success(SuccessCode.SELECT_SUCCESS, messages);
    }

    @PostMapping("/{roomId}/close")
    @PreAuthorize("@consultationGuard.isParticipantMember(#roomId, #userDetails.memberId)")
    public CommonResponse<Void> closeRoom(@PathVariable Long roomId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        consultationRoomService.closeRoom(roomId, userDetails.getMemberId());

        return CommonResponse.success(SuccessCode.SUCCESS, null);
    }
}

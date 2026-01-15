package org.aibe4.dodeul.domain.consultation.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consultation.model.dto.MessageDto;
import org.aibe4.dodeul.domain.consultation.service.ChatService;
import org.aibe4.dodeul.global.file.model.dto.response.FileUploadResponse;
import org.aibe4.dodeul.global.file.service.FileService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/consultations/room")
@RequiredArgsConstructor
public class ChatApiController {

    private final ChatService chatService;
    private final FileService fileService;

    @GetMapping("/{roomId}/messages")
    @PreAuthorize("@consultationGuard.isParticipantMember(#roomId, #userDetails.memberId)")
    public CommonResponse<Slice<MessageDto>> getMoreMessages(@PathVariable Long roomId,
                                                             @RequestParam(required = false) Long lastMessageId,
                                                             @RequestParam(defaultValue = "20") int size,
                                                             @AuthenticationPrincipal CustomUserDetails userDetails) {

        Slice<MessageDto> messages = chatService.getMoreMessages(roomId, lastMessageId, size);
        return CommonResponse.success(SuccessCode.SELECT_SUCCESS, messages);
    }

    @PostMapping("/{roomId}/file/upload")
    @PreAuthorize("@consultationGuard.isParticipantMember(#roomId, #userDetails.memberId)")
    public CommonResponse<FileUploadResponse> uploadFile(@PathVariable Long roomId,
                                                 @RequestParam("file") MultipartFile file,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {

        FileUploadResponse uploadResponse = fileService.upload(file, "chat");

        return CommonResponse.success(SuccessCode.SUCCESS, uploadResponse);
    }
}

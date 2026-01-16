package org.aibe4.dodeul.domain.consultation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consultation.model.dto.MessageDto;
import org.aibe4.dodeul.domain.consultation.service.ChatService;
import org.aibe4.dodeul.global.file.model.dto.response.FileUploadResponse;
import org.aibe4.dodeul.global.file.service.FileService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Consultation", description = "상담방 채팅 메시지 내역 조회 및 파일 업로드 API")
@RestController
@RequestMapping("/api/consultations/room")
@RequiredArgsConstructor
public class ChatApiController {

    private final ChatService chatService;
    private final FileService fileService;

    @Operation(
        summary = "이전 메시지 조회 (무한 스크롤)",
        description =
            """
                채팅방의 이전 메시지 내역을 조회합니다 (Cursor Pagination).

                - `lastMessageId`가 없으면 가장 최신 메시지부터 조회
                - `lastMessageId`가 있으면 그 메시지보다 더 오래된 메시지를 조회
                """)
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content),
        @ApiResponse(responseCode = "403", description = "권한 없음 (참여자가 아님)", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{roomId}/messages")
    @PreAuthorize("@consultationGuard.isParticipantMember(#roomId, #userDetails.memberId)")
    public CommonResponse<Slice<MessageDto>> getMoreMessages(
        @Parameter(description = "채팅방 ID", required = true)
        @PathVariable Long roomId,

        @Parameter(description = "마지막으로 로드된 메시지 ID (첫 로드 시 null)", example = "150")
        @RequestParam(required = false) Long lastMessageId,

        @Parameter(description = "가져올 메시지 개수", example = "20")
        @RequestParam(defaultValue = "20") int size,

        @AuthenticationPrincipal CustomUserDetails userDetails) {

        Slice<MessageDto> messages = chatService.getMoreMessages(roomId, lastMessageId, size);
        return CommonResponse.success(SuccessCode.SELECT_SUCCESS, messages);
    }

    @Operation(
        summary = "채팅 파일 업로드",
        description =
            """
                채팅방에 보낼 파일(이미지 등)을 업로드합니다.

                - 주의: 이 API는 파일을 스토리지에 저장하고 URL을 반환할 뿐, 메시지를 전송하지 않습니다.
                - 클라이언트는 반환된 `fileUrl`을 사용하여 소켓(`sendMessage`)으로 메시지를 보내야 합니다.
                """)
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "400", description = "파일이 비어있거나 형식이 잘못됨", content = @Content),
        @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content),
        @ApiResponse(responseCode = "403", description = "권한 없음 (참여자가 아님)", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    // Swagger에서 파일 업로더가 뜨도록 consumes 설정 추가
    @PostMapping(value = "/{roomId}/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@consultationGuard.isParticipantMember(#roomId, #userDetails.memberId)")
    public CommonResponse<FileUploadResponse> uploadFile(
        @Parameter(description = "채팅방 ID", required = true)
        @PathVariable Long roomId,

        @Parameter(description = "업로드할 파일", required = true)
        @RequestParam("file") MultipartFile file,

        @AuthenticationPrincipal CustomUserDetails userDetails) {

        FileUploadResponse uploadResponse = fileService.upload(file, "chat");

        return CommonResponse.success(SuccessCode.SUCCESS, uploadResponse);
    }
}

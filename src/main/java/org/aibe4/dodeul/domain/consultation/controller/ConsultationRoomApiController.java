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
import org.aibe4.dodeul.domain.consultation.service.ConsultationRoomService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Consultation", description = "상담 채팅방 관리 API")
@RestController
@RequestMapping("/api/consultations/room")
@RequiredArgsConstructor
public class ConsultationRoomApiController {

    private final ConsultationRoomService consultationRoomService;
    private final ChatService chatService;

    @Operation(
        summary = "상담 종료",
        description =
            """
                진행 중인 상담 채팅방을 종료합니다.

                - 로그인 필요
                - 해당 채팅방의 참여자(멘토 또는 멘티)만 종료 가능
                - 종료 시 더 이상 채팅을 보낼 수 없습니다.
                """)
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
        @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content),
        @ApiResponse(responseCode = "403", description = "권한 없음 (참여자가 아님)", content = @Content),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 채팅방", content = @Content),
        @ApiResponse(responseCode = "409", description = "이미 종료된 상담", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{roomId}/close")
    @PreAuthorize("@consultationGuard.isParticipantMember(#roomId, #userDetails.memberId)")
    public CommonResponse<Void> closeRoom(
        @Parameter(description = "채팅방 ID", required = true)
        @PathVariable Long roomId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        consultationRoomService.closeRoom(roomId, userDetails.getMemberId());

        return CommonResponse.success(SuccessCode.SUCCESS, null);
    }

    @Operation(
        summary = "채팅방 파일 목록 조회",
        description =
            """
                채팅방에서 공유된 파일(이미지, 문서 등) 목록을 조회합니다.

                - 최신순 정렬
                - 메시지 타입이 IMAGE 또는 FILE인 것만 조회
                """)
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
        @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content),
        @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 채팅방", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{roomId}/files")
    public CommonResponse<List<MessageDto>> getChatFiles(
        @Parameter(description = "채팅방 ID", required = true)
        @PathVariable Long roomId) {

        // 메시지 중 type이 IMAGE 또는 FILE인 것들만 최신순으로 조회
        List<MessageDto> files = chatService.getChatFiles(roomId);
        return CommonResponse.success(SuccessCode.SUCCESS, files);
    }

}

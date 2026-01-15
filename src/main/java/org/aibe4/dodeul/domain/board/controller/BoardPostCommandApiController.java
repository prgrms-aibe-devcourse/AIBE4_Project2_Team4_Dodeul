// src/main/java/org/aibe4/dodeul/domain/board/controller/BoardPostCommandApiController.java
package org.aibe4.dodeul.domain.board.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostUpdateRequest;
import org.aibe4.dodeul.domain.board.service.BoardPostCommandService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Board", description = "게시판 게시글 수정/삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/posts")
public class BoardPostCommandApiController {

    private final BoardPostCommandService boardPostCommandService;

    @Operation(
        summary = "게시글 수정",
        description = """
            게시글을 수정합니다.

            - 로그인 필수
            - 작성자 본인만 수정 가능
            - 제목, 본문, 상담분야(consultingTag) 수정 지원
            - 스킬 태그는 별도 API로 관리
            """)
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "요청값 검증 실패 (제목/본문 공백 등)",
            content = @Content),
        @ApiResponse(
            responseCode = "401",
            description = "인증 필요 (로그인하지 않음)",
            content = @Content),
        @ApiResponse(
            responseCode = "403",
            description = "권한 없음 (작성자가 아님)",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "게시글을 찾을 수 없음 (삭제되었거나 존재하지 않음)",
            content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{postId}")
    public CommonResponse<Void> update(
        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
        @Parameter(description = "게시글 ID", example = "1", required = true)
        @PathVariable Long postId,
        @RequestBody @Valid BoardPostUpdateRequest request) {

        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        boardPostCommandService.update(postId, memberId, request);
        return CommonResponse.success(SuccessCode.SUCCESS, null, "게시글 수정 성공");
    }

    @Operation(
        summary = "게시글 삭제",
        description = """
            게시글을 삭제합니다.

            - 로그인 필수
            - 작성자 본인만 삭제 가능
            - 실제로는 DELETED 상태로 변경 (소프트 삭제)
            - 삭제된 게시글은 목록/상세 조회에서 제외됨
            """)
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "삭제 성공",
            content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "인증 필요 (로그인하지 않음)",
            content = @Content),
        @ApiResponse(
            responseCode = "403",
            description = "권한 없음 (작성자가 아님)",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "게시글을 찾을 수 없음 (이미 삭제되었거나 존재하지 않음)",
            content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{postId}")
    public CommonResponse<Void> delete(
        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
        @Parameter(description = "게시글 ID", example = "1", required = true)
        @PathVariable Long postId) {

        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        boardPostCommandService.delete(postId, memberId);
        return CommonResponse.success(SuccessCode.SUCCESS, null, "게시글 삭제 성공");
    }
}

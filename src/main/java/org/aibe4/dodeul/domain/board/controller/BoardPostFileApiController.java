// src/main/java/org/aibe4/dodeul/domain/board/controller/BoardPostFileApiController.java
package org.aibe4.dodeul.domain.board.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostFileCreateRequest;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostFileResponse;
import org.aibe4.dodeul.domain.board.service.BoardPostFileService;
import org.aibe4.dodeul.domain.common.model.entity.CommonFile;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Board", description = "공개 게시판(QnA) 게시글 첨부파일 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/posts")
public class BoardPostFileApiController {

    private final BoardPostFileService boardPostFileService;

    @Operation(
        summary = "게시글 첨부파일 등록",
        description = """
            게시글에 첨부파일을 등록합니다.

            - 로그인 필수
            - 게시글 작성자만 파일 등록 가능
            - 실제 파일 업로드는 별도 FileService를 통해 선행 완료 후 파일 정보만 등록
            - 요청 본문에 파일 URL, 원본 파일명, 콘텐츠 타입, 파일 크기 포함
            """)
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "파일 등록 성공",
            content =
            @Content(
                schema = @Schema(implementation = org.aibe4.dodeul.global.response.CommonResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "요청값 검증 실패 (파일 정보 누락 등)",
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
            description = "게시글을 찾을 수 없음",
            content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{postId}/files")
    public CommonResponse<BoardPostFileResponse> addFiles(
        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
        @Parameter(description = "게시글 ID", example = "1", required = true)
        @PathVariable Long postId,
        @RequestBody @Validated BoardPostFileCreateRequest request) {

        Long memberId = userDetails == null ? null : userDetails.getMemberId();

        List<CommonFile> saved = boardPostFileService.addFiles(memberId, postId, request);
        return CommonResponse.success(
            SuccessCode.SUCCESS, BoardPostFileResponse.of(postId, saved), "파일 등록 성공");
    }

    @Operation(
        summary = "게시글 첨부파일 조회",
        description = """
            게시글의 첨부파일 목록을 조회합니다.

            - 비로그인 사용자도 조회 가능
            - 파일 ID, URL, 원본 파일명, 콘텐츠 타입, 크기, 생성일시 반환
            """)
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content =
            @Content(
                schema = @Schema(implementation = org.aibe4.dodeul.global.response.CommonResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "게시글을 찾을 수 없음",
            content = @Content)
    })
    @GetMapping("/{postId}/files")
    public CommonResponse<BoardPostFileResponse> getFiles(
        @Parameter(description = "게시글 ID", example = "1", required = true)
        @PathVariable Long postId) {

        List<CommonFile> files = boardPostFileService.getFiles(postId);
        return CommonResponse.success(
            SuccessCode.SUCCESS, BoardPostFileResponse.of(postId, files), "파일 조회 성공");
    }

    @Operation(
        summary = "게시글 첨부파일 삭제",
        description = """
            게시글의 특정 첨부파일을 삭제합니다.

            - 로그인 필수
            - 게시글 작성자만 파일 삭제 가능
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
            description = "게시글 또는 파일을 찾을 수 없음",
            content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{postId}/files/{fileId}")
    public CommonResponse<Void> deleteFile(
        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
        @Parameter(description = "게시글 ID", example = "1", required = true)
        @PathVariable Long postId,
        @Parameter(description = "파일 ID", example = "5", required = true)
        @PathVariable Long fileId) {

        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        boardPostFileService.deleteFile(memberId, postId, fileId);
        return CommonResponse.success(SuccessCode.SUCCESS, null, "파일 삭제 성공");
    }
}

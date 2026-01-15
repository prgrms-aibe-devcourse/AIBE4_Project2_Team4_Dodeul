// src/main/java/org/aibe4/dodeul/domain/board/controller/BoardPostScrapApiController.java
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
import org.aibe4.dodeul.domain.board.exception.BoardPolicyException;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostScrapStatusResponse;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostScrapToggleResponse;
import org.aibe4.dodeul.domain.board.model.dto.response.MyScrapListResponse;
import org.aibe4.dodeul.domain.board.service.BoardPostScrapService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "BoardPost Scrap", description = "게시판 스크랩 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardPostScrapApiController {

    private final BoardPostScrapService boardPostScrapService;

    @Operation(
        summary = "게시글 스크랩 토글",
        description = """
            게시글을 스크랩하거나 취소합니다.

            - 로그인 필수
            - 이미 스크랩한 경우: 스크랩 취소
            - 스크랩하지 않은 경우: 스크랩 추가
            - 응답에 현재 스크랩 여부와 전체 스크랩 수 포함
            """)
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "스크랩 처리 성공",
            content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "인증 필요 (로그인하지 않음)",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "게시글을 찾을 수 없음 (삭제되었거나 존재하지 않음)",
            content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/board/posts/{postId}/scrap")
    public CommonResponse<BoardPostScrapToggleResponse> toggle(
        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
        @Parameter(description = "게시글 ID", example = "1", required = true)
        @PathVariable Long postId) {

        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        if (memberId == null) {
            throw new BoardPolicyException(ErrorCode.UNAUTHORIZED_ACCESS, "로그인이 필요합니다.");
        }

        BoardPostScrapToggleResponse data = boardPostScrapService.toggle(memberId, postId);
        return CommonResponse.success(SuccessCode.SUCCESS, data, "스크랩 처리 성공");
    }

    @Operation(
        summary = "게시글 스크랩 상태 조회",
        description = """
            특정 게시글의 스크랩 상태를 조회합니다.

            - 비로그인 사용자도 조회 가능 (단, scrappedByMe는 항상 false)
            - 로그인한 경우 내 스크랩 여부 확인 가능
            - 전체 스크랩 수 조회 가능
            """)
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "게시글을 찾을 수 없음",
            content = @Content)
    })
    @GetMapping("/board/posts/{postId}/scrap")
    public CommonResponse<BoardPostScrapStatusResponse> status(
        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
        @Parameter(description = "게시글 ID", example = "1", required = true)
        @PathVariable Long postId) {

        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        BoardPostScrapStatusResponse data = boardPostScrapService.getStatus(memberId, postId);
        return CommonResponse.success(SuccessCode.SUCCESS, data, "스크랩 상태 조회 성공");
    }

    @Operation(
        summary = "게시글 스크랩 취소",
        description = """
            게시글 스크랩을 명시적으로 취소합니다.

            - 로그인 필수
            - 스크랩하지 않은 게시글에 대해 호출해도 에러 없음 (멱등성 보장)
            - 응답에 현재 스크랩 여부(false)와 전체 스크랩 수 포함
            """)
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "스크랩 취소 성공",
            content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "인증 필요 (로그인하지 않음)",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "게시글을 찾을 수 없음",
            content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/board/posts/{postId}/scrap")
    public CommonResponse<BoardPostScrapToggleResponse> delete(
        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
        @Parameter(description = "게시글 ID", example = "1", required = true)
        @PathVariable Long postId) {

        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        if (memberId == null) {
            throw new BoardPolicyException(ErrorCode.UNAUTHORIZED_ACCESS, "로그인이 필요합니다.");
        }

        BoardPostScrapToggleResponse data = boardPostScrapService.delete(memberId, postId);
        return CommonResponse.success(SuccessCode.SUCCESS, data, "스크랩 취소 성공");
    }

    @Operation(
        summary = "내 스크랩 목록 조회",
        description = """
            로그인한 사용자가 스크랩한 게시글 목록을 조회합니다.

            - 로그인 필수
            - 삭제된 게시글은 목록에서 제외
            - 각 항목에 제목, 작성자, 날짜, 스킬 태그 포함
            - 최신 스크랩순으로 정렬
            """)
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = CommonResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "인증 필요 (로그인하지 않음)",
            content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/mypage/scraps")
    public CommonResponse<MyScrapListResponse> myScraps(
        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        if (memberId == null) {
            throw new BoardPolicyException(ErrorCode.UNAUTHORIZED_ACCESS, "로그인이 필요합니다.");
        }

        MyScrapListResponse data = boardPostScrapService.getMyScraps(memberId);
        return CommonResponse.success(SuccessCode.SUCCESS, data, "내 스크랩 목록 조회 성공");
    }
}

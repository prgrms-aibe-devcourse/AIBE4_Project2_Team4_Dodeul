// src/main/java/org/aibe4/dodeul/domain/board/controller/BoardCommentApiController.java
package org.aibe4.dodeul.domain.board.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.comment.BoardCommentCreateRequest;
import org.aibe4.dodeul.domain.board.model.dto.comment.BoardCommentListResponse;
import org.aibe4.dodeul.domain.board.model.dto.comment.BoardCommentUpdateRequest;
import org.aibe4.dodeul.domain.board.service.BoardCommentService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Board Comment API", description = "게시판 댓글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
@Validated
public class BoardCommentApiController {

    private final BoardCommentService boardCommentService;

    @Operation(summary = "댓글 목록 조회", description = "특정 게시글의 댓글 목록을 조회합니다.")
    @GetMapping("/posts/{postId}/comments")
    public CommonResponse<BoardCommentListResponse> getComments(
        @Parameter(description = "게시글 ID", example = "1") @PathVariable Long postId,
        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = (userDetails == null) ? null : userDetails.getMemberId();
        BoardCommentListResponse data = boardCommentService.getComments(postId, memberId);
        return CommonResponse.success(SuccessCode.SUCCESS, data, "댓글 목록 조회 성공");
    }

    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "댓글 작성 성공"),
        @ApiResponse(responseCode = "401", description = "로그인 필요"),
        @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @PostMapping("/posts/{postId}/comments")
    public CommonResponse<Void> createComment(
        @Parameter(description = "게시글 ID", example = "1") @PathVariable Long postId,
        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody BoardCommentCreateRequest request) {
        Long memberId = (userDetails == null) ? null : userDetails.getMemberId();
        boardCommentService.createComment(postId, memberId, request);
        return CommonResponse.success(SuccessCode.SUCCESS, null, "댓글 작성 성공");
    }

    @Operation(summary = "댓글 수정", description = "작성한 댓글을 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음 (작성자 아님)"),
        @ApiResponse(responseCode = "404", description = "댓글 없음")
    })
    @PatchMapping("/comments/{commentId}")
    public CommonResponse<Void> updateComment(
        @Parameter(description = "댓글 ID", example = "10") @PathVariable Long commentId,
        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody BoardCommentUpdateRequest request) {
        Long memberId = (userDetails == null) ? null : userDetails.getMemberId();
        boardCommentService.updateComment(commentId, memberId, request);
        return CommonResponse.success(SuccessCode.SUCCESS, null, "댓글 수정 성공");
    }

    @Operation(summary = "댓글 삭제", description = "작성한 댓글을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음 (작성자 아님)"),
        @ApiResponse(responseCode = "404", description = "댓글 없음")
    })
    @DeleteMapping("/comments/{commentId}")
    public CommonResponse<Void> deleteComment(
        @Parameter(description = "댓글 ID", example = "10") @PathVariable Long commentId,
        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = (userDetails == null) ? null : userDetails.getMemberId();
        boardCommentService.deleteComment(commentId, memberId);
        return CommonResponse.success(SuccessCode.SUCCESS, null, "댓글 삭제 성공");
    }

    @Operation(summary = "댓글 좋아요 토글", description = "댓글에 좋아요를 누르거나 취소합니다.")
    @PostMapping("/comments/{commentId}/likes/toggle")
    public CommonResponse<Void> toggleLike(
        @Parameter(description = "댓글 ID", example = "10") @PathVariable Long commentId,
        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = (userDetails == null) ? null : userDetails.getMemberId();
        boardCommentService.toggleLike(commentId, memberId);
        return CommonResponse.success(SuccessCode.SUCCESS, null, "댓글 좋아요 토글 성공");
    }
}

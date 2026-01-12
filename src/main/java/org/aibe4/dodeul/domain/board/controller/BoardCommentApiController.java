// src/main/java/org/aibe4/dodeul/domain/board/controller/BoardCommentApiController.java
package org.aibe4.dodeul.domain.board.controller;

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

    @GetMapping("/posts/{postId}/comments")
    public CommonResponse<BoardCommentListResponse> getComments(
        @PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = (userDetails == null) ? null : userDetails.getMemberId();
        BoardCommentListResponse data = boardCommentService.getComments(postId, memberId);
        return CommonResponse.success(SuccessCode.SUCCESS, data, "댓글 목록 조회 성공");
    }

    @PostMapping("/posts/{postId}/comments")
    public CommonResponse<Void> createComment(
        @PathVariable Long postId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody BoardCommentCreateRequest request) {
        Long memberId = (userDetails == null) ? null : userDetails.getMemberId();
        boardCommentService.createComment(postId, memberId, request);
        return CommonResponse.success(SuccessCode.SUCCESS, null, "댓글 작성 성공");
    }

    @PatchMapping("/comments/{commentId}")
    public CommonResponse<Void> updateComment(
        @PathVariable Long commentId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody BoardCommentUpdateRequest request) {
        Long memberId = (userDetails == null) ? null : userDetails.getMemberId();
        boardCommentService.updateComment(commentId, memberId, request);
        return CommonResponse.success(SuccessCode.SUCCESS, null, "댓글 수정 성공");
    }

    @DeleteMapping("/comments/{commentId}")
    public CommonResponse<Void> deleteComment(
        @PathVariable Long commentId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = (userDetails == null) ? null : userDetails.getMemberId();
        boardCommentService.deleteComment(commentId, memberId);
        return CommonResponse.success(SuccessCode.SUCCESS, null, "댓글 삭제 성공");
    }

    @PostMapping("/comments/{commentId}/likes/toggle")
    public CommonResponse<Void> toggleLike(
        @PathVariable Long commentId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = (userDetails == null) ? null : userDetails.getMemberId();
        boardCommentService.toggleLike(commentId, memberId);
        return CommonResponse.success(SuccessCode.SUCCESS, null, "댓글 좋아요 토글 성공");
    }
}

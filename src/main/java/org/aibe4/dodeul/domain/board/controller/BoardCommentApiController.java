package org.aibe4.dodeul.domain.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.comment.BoardCommentCreateRequest;
import org.aibe4.dodeul.domain.board.model.dto.comment.BoardCommentListResponse;
import org.aibe4.dodeul.domain.board.model.dto.comment.BoardCommentUpdateRequest;
import org.aibe4.dodeul.domain.board.service.BoardCommentService;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardCommentApiController {

    private final BoardCommentService boardCommentService;

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<BoardCommentListResponse> getComments(
        @PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = (userDetails == null) ? null : userDetails.getMemberId();
        return ResponseEntity.ok(boardCommentService.getComments(postId, memberId));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Map<String, Object>> createComment(
        @PathVariable Long postId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody BoardCommentCreateRequest request) {
        Long memberId = (userDetails == null) ? null : userDetails.getMemberId();
        boardCommentService.createComment(postId, memberId, request);
        return ResponseEntity.ok(Map.of());
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> updateComment(
        @PathVariable Long commentId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody BoardCommentUpdateRequest request) {
        Long memberId = (userDetails == null) ? null : userDetails.getMemberId();
        boardCommentService.updateComment(commentId, memberId, request);
        return ResponseEntity.ok(Map.of());
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> deleteComment(
        @PathVariable Long commentId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = (userDetails == null) ? null : userDetails.getMemberId();
        boardCommentService.deleteComment(commentId, memberId);
        return ResponseEntity.ok(Map.of());
    }

    @PostMapping("/comments/{commentId}/likes/toggle")
    public ResponseEntity<Map<String, Object>> toggleLike(
        @PathVariable Long commentId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = (userDetails == null) ? null : userDetails.getMemberId();
        boardCommentService.toggleLike(commentId, memberId);
        return ResponseEntity.ok(Map.of());
    }
}

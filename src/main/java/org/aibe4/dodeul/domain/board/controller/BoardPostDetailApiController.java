package org.aibe4.dodeul.domain.board.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostDetailResponse;
import org.aibe4.dodeul.domain.board.service.BoardPostDetailService;
import org.aibe4.dodeul.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/posts")
public class BoardPostDetailApiController {

    private final BoardPostDetailService boardPostDetailService;

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<BoardPostDetailResponse>> getDetail(
            @PathVariable Long postId) {
        try {
            BoardPostDetailResponse data = boardPostDetailService.getDetail(postId);
            return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK.value(), "게시글 상세 조회 성공", data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        }
    }
}

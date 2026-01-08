// src/main/java/org/aibe4/dodeul/domain/board/controller/BoardPostDetailApiController.java
package org.aibe4.dodeul.domain.board.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostDetailResponse;
import org.aibe4.dodeul.domain.board.service.BoardPostDetailService;
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
    public ResponseEntity<ApiResult<BoardPostDetailResponse>> getDetail(@PathVariable Long postId) {
        try {
            BoardPostDetailResponse data = boardPostDetailService.getDetail(postId);
            return ResponseEntity.ok(ApiResult.ok("게시글 상세 조회 성공", data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResult.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
    }

    public static class ApiResult<T> {

        private final int code;
        private final String message;
        private final T data;

        private ApiResult(int code, String message, T data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }

        public static <T> ApiResult<T> ok(String message, T data) {
            return new ApiResult<>(HttpStatus.OK.value(), message, data);
        }

        public static <T> ApiResult<T> error(int code, String message) {
            return new ApiResult<>(code, message, null);
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public T getData() {
            return data;
        }
    }
}

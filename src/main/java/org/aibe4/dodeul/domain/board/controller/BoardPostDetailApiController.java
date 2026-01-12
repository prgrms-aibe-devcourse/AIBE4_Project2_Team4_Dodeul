// src/main/java/org/aibe4/dodeul/domain/board/controller/BoardPostDetailApiController.java
package org.aibe4.dodeul.domain.board.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostDetailResponse;
import org.aibe4.dodeul.domain.board.service.BoardPostDetailService;
import org.aibe4.dodeul.domain.board.service.BoardPostService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Board Post Detail API", description = "게시판 게시글 상세 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/posts")
public class BoardPostDetailApiController {

    private final BoardPostDetailService boardPostDetailService;
    private final BoardPostService boardPostService;

    @GetMapping("/{postId}")
    public CommonResponse<BoardPostDetailResponse> getDetail(@PathVariable Long postId) {
        boardPostService.increaseViewCount(postId);
        BoardPostDetailResponse data = boardPostDetailService.getDetail(postId);
        return CommonResponse.success(SuccessCode.SUCCESS, data, "게시글 상세 조회 성공");
    }
}

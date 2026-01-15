// src/main/java/org/aibe4/dodeul/domain/board/controller/BoardPostScrapApiController.java
package org.aibe4.dodeul.domain.board.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostScrapStatusResponse;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostScrapToggleResponse;
import org.aibe4.dodeul.domain.board.model.dto.response.MyScrapListResponse;
import org.aibe4.dodeul.domain.board.service.BoardPostScrapService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardPostScrapApiController {

    private final BoardPostScrapService boardPostScrapService;

    @PostMapping("/board/posts/{postId}/scrap")
    public CommonResponse<BoardPostScrapToggleResponse> toggle(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long postId) {
        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        if (memberId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        BoardPostScrapToggleResponse data = boardPostScrapService.toggle(memberId, postId);
        return CommonResponse.success(SuccessCode.SUCCESS, data, "스크랩 처리 성공");
    }

    @GetMapping("/board/posts/{postId}/scrap")
    public CommonResponse<BoardPostScrapStatusResponse> status(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long postId) {
        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        BoardPostScrapStatusResponse data = boardPostScrapService.getStatus(memberId, postId);
        return CommonResponse.success(SuccessCode.SUCCESS, data, "스크랩 상태 조회 성공");
    }

    @DeleteMapping("/board/posts/{postId}/scrap")
    public CommonResponse<BoardPostScrapToggleResponse> delete(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long postId) {
        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        if (memberId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        BoardPostScrapToggleResponse data = boardPostScrapService.delete(memberId, postId);
        return CommonResponse.success(SuccessCode.SUCCESS, data, "스크랩 취소 성공");
    }

    @GetMapping("/mypage/scraps")
    public CommonResponse<MyScrapListResponse> myScraps(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        if (memberId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        MyScrapListResponse data = boardPostScrapService.getMyScraps(memberId);
        return CommonResponse.success(SuccessCode.SUCCESS, data, "내 스크랩 목록 조회 성공");
    }
}

// src/main/java/org/aibe4/dodeul/domain/board/controller/BoardPostScrapApiController.java
package org.aibe4.dodeul.domain.board.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostScrapStatusResponse;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostScrapToggleResponse;
import org.aibe4.dodeul.domain.board.model.dto.response.MyScrapListResponse;
import org.aibe4.dodeul.domain.board.service.BoardPostScrapService;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardPostScrapApiController {

    private final BoardPostScrapService boardPostScrapService;

    @PostMapping("/board/posts/{postId}/scrap")
    public BoardPostScrapToggleResponse toggle(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long postId) {
        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        if (memberId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return boardPostScrapService.toggle(memberId, postId);
    }

    @GetMapping("/board/posts/{postId}/scrap")
    public BoardPostScrapStatusResponse status(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long postId) {
        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        return boardPostScrapService.getStatus(memberId, postId);
    }

    @DeleteMapping("/board/posts/{postId}/scrap")
    public BoardPostScrapToggleResponse delete(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long postId) {
        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        if (memberId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return boardPostScrapService.delete(memberId, postId);
    }

    @GetMapping("/mypage/scraps")
    public MyScrapListResponse myScraps(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        if (memberId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return boardPostScrapService.getMyScraps(memberId);
    }
}

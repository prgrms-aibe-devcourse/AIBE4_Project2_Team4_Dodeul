// src/main/java/org/aibe4/dodeul/domain/board/controller/BoardPostCommandApiController.java
package org.aibe4.dodeul.domain.board.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostUpdateRequest;
import org.aibe4.dodeul.domain.board.service.BoardPostCommandService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Board Post Command API", description = "게시판 게시글 수정/삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/posts")
public class BoardPostCommandApiController {

    private final BoardPostCommandService boardPostCommandService;

    @PatchMapping("/{postId}")
    public CommonResponse<Void> update(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Long postId,
        @RequestBody @Valid BoardPostUpdateRequest request) {

        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        boardPostCommandService.update(postId, memberId, request);
        return CommonResponse.success(SuccessCode.SUCCESS, null, "게시글 수정 성공");
    }

    @DeleteMapping("/{postId}")
    public CommonResponse<Void> delete(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long postId) {

        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        boardPostCommandService.delete(postId, memberId);
        return CommonResponse.success(SuccessCode.SUCCESS, null, "게시글 삭제 성공");
    }
}

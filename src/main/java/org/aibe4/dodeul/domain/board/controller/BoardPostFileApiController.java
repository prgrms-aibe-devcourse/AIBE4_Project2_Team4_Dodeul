package org.aibe4.dodeul.domain.board.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostFileCreateRequest;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostFileResponse;
import org.aibe4.dodeul.domain.board.service.BoardPostFileService;
import org.aibe4.dodeul.domain.common.model.entity.CommonFile;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "BoardPostFile", description = "공개 게시판(QnA) 게시글 파일 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/posts")
public class BoardPostFileApiController {

    private final BoardPostFileService boardPostFileService;

    @PostMapping("/{postId}/files")
    public CommonResponse<BoardPostFileResponse> addFiles(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Long postId,
        @RequestBody @Validated BoardPostFileCreateRequest request) {
        Long memberId = userDetails == null ? null : userDetails.getMemberId();

        List<CommonFile> saved = boardPostFileService.addFiles(memberId, postId, request);
        return CommonResponse.success(
            SuccessCode.SUCCESS, BoardPostFileResponse.of(postId, saved), "파일 등록 성공");
    }

    @GetMapping("/{postId}/files")
    public CommonResponse<BoardPostFileResponse> getFiles(@PathVariable Long postId) {
        List<CommonFile> files = boardPostFileService.getFiles(postId);
        return CommonResponse.success(
            SuccessCode.SUCCESS, BoardPostFileResponse.of(postId, files), "파일 조회 성공");
    }

    @DeleteMapping("/{postId}/files/{fileId}")
    public CommonResponse<Void> deleteFile(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Long postId,
        @PathVariable Long fileId) {
        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        boardPostFileService.deleteFile(memberId, postId, fileId);
        return CommonResponse.success(SuccessCode.SUCCESS, null, "파일 삭제 성공");
    }
}

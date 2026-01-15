// src/main/java/org/aibe4/dodeul/domain/board/controller/BoardPostDetailApiController.java
package org.aibe4.dodeul.domain.board.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostDetailResponse;
import org.aibe4.dodeul.domain.board.service.BoardPostDetailService;
import org.aibe4.dodeul.domain.board.service.BoardPostService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Board", description = "게시판 게시글 상세 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/posts")
public class BoardPostDetailApiController {

    private final BoardPostDetailService boardPostDetailService;
    private final BoardPostService boardPostService;

    @Operation(
        summary = "게시글 상세 조회",
        description = """
            게시글의 상세 정보를 조회합니다.

            - 비로그인 사용자도 조회 가능
            - 조회 시 자동으로 조회수(viewCount) 증가
            - 로그인한 경우 본인 작성 여부, 스크랩 여부 등 추가 정보 제공
            - 삭제된(DELETED) 게시글은 조회 불가
            - 반환 데이터: 제목, 본문, 작성자 정보, 스킬 태그, 파일, 상태 등
            """)
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content =
            @Content(
                schema = @Schema(implementation = org.aibe4.dodeul.global.response.CommonResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "게시글을 찾을 수 없음 (삭제되었거나 존재하지 않음)",
            content = @Content)
    })
    @GetMapping("/{postId}")
    public CommonResponse<BoardPostDetailResponse> getDetail(
        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
        @Parameter(description = "게시글 ID", example = "1", required = true)
        @PathVariable Long postId) {

        Long memberId = userDetails == null ? null : userDetails.getMemberId();

        boardPostService.increaseViewCount(postId);
        BoardPostDetailResponse data = boardPostDetailService.getDetail(postId, memberId);
        return CommonResponse.success(SuccessCode.SUCCESS, data, "게시글 상세 조회 성공");
    }
}

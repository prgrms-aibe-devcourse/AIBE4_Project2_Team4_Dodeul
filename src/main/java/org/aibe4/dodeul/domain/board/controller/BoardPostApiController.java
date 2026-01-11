package org.aibe4.dodeul.domain.board.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostCreateRequest;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostListRequest;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostCreateResponse;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostListPageResponse;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostListResponse;
import org.aibe4.dodeul.domain.board.service.BoardPostService;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "BoardPost", description = "공개 게시판(QnA) 게시글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/posts")
public class BoardPostApiController {

    private final BoardPostService boardPostService;

    /**
     * 게시글 목록 조회
     *
     * <p>정책: 비로그인 허용은 "기본 목록"까지만, 검색/필터/정렬 변경 시 로그인 필요
     */
    @Operation(
        summary = "게시글 목록 조회",
        description =
            """
                게시글 목록을 조회합니다.

                - 비로그인 허용: 기본 목록(필터/검색/정렬 변경 없음)만 가능
                - 로그인 필요: keyword/status/tagIds/sort 중 하나라도 사용하면 로그인 필요
                """)
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "성공",
            content =
            @Content(
                schema =
                @Schema(
                    implementation =
                        org.aibe4.dodeul.global.response.CommonResponse.class))),
        @ApiResponse(responseCode = "400", description = "요청값 검증 실패", content = @Content),
        @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content),
        @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public CommonResponse<BoardPostListPageResponse> listPosts(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Parameter(
            in = ParameterIn.QUERY,
            description = "상담 분야(카테고리)",
            schema = @Schema(implementation = ConsultingTag.class))
        @RequestParam(value = "consultingTag", required = false)
        ConsultingTag consultingTag,
        @Parameter(
            in = ParameterIn.QUERY,
            description = "스킬 태그 ID 목록",
            schema = @Schema(type = "array", implementation = Long.class))
        @RequestParam(value = "tagIds", required = false)
        List<Long> tagIds,
        @Parameter(
            in = ParameterIn.QUERY,
            description = "게시글 상태(없거나 잘못되면 OPEN으로 처리)",
            schema = @Schema(allowableValues = {"OPEN", "CLOSED", "DELETED"}, defaultValue = "OPEN"))
        @RequestParam(value = "status", required = false)
        String status,
        @Parameter(
            in = ParameterIn.QUERY,
            description = "검색 키워드(제목/본문 등 정책에 따라 적용)",
            schema = @Schema(maxLength = 255))
        @RequestParam(value = "keyword", required = false)
        String keyword,
        @Parameter(
            in = ParameterIn.QUERY,
            description = "정렬 기준",
            schema =
            @Schema(
                allowableValues = {"LATEST", "LIKES", "SCRAPS"},
                defaultValue = "LATEST"))
        @RequestParam(value = "sort", required = false)
        String sort,
        @Parameter(
            in = ParameterIn.QUERY,
            description = "페이지 번호(0부터 시작)",
            schema = @Schema(defaultValue = "0", minimum = "0"))
        @RequestParam(value = "page", required = false, defaultValue = "0")
        Integer page,
        @Parameter(
            in = ParameterIn.QUERY,
            description = "페이지 크기",
            schema = @Schema(defaultValue = "20", minimum = "1", maximum = "100"))
        @RequestParam(value = "size", required = false, defaultValue = "20")
        Integer size) {

        Long memberId = userDetails == null ? null : userDetails.getMemberId();

        BoardPostListRequest request =
            BoardPostListRequest.builder()
                .consultingTag(consultingTag)
                .tagIds(tagIds)
                .status(status)
                .keyword(keyword)
                .sort(sort)
                .build();

        Pageable pageable = PageRequest.of(page, size, toSpringSort(sort));

        Page<BoardPostListResponse> result = boardPostService.getPosts(request, memberId, pageable);
        BoardPostListPageResponse data = BoardPostListPageResponse.from(result);

        return CommonResponse.success(SuccessCode.SUCCESS, data, "게시글 목록 조회 성공");
    }

    @Operation(
        summary = "게시글 작성",
        description =
            """
                게시글을 작성합니다.

                - 로그인 필요
                - 카테고리(consultingTagId), 제목/본문, 스킬태그(skillTagIds)는 요청 바디로 전달
                """)
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "성공",
            content =
            @Content(
                schema =
                @Schema(
                    implementation =
                        org.aibe4.dodeul.global.response.CommonResponse.class))),
        @ApiResponse(responseCode = "400", description = "요청값 검증 실패", content = @Content),
        @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content),
        @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public CommonResponse<BoardPostCreateResponse> createPost(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody @Validated BoardPostCreateRequest request) {

        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        Long postId = boardPostService.createPost(memberId, request);

        return CommonResponse.success(
            SuccessCode.SUCCESS, new BoardPostCreateResponse(postId), "게시글 작성 성공");
    }

    private Sort toSpringSort(String sort) {
        if (sort == null || sort.isBlank() || "LATEST".equalsIgnoreCase(sort)) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        if ("LIKES".equalsIgnoreCase(sort)) {
            return Sort.by(Sort.Direction.DESC, "likeCount").and(Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        if ("SCRAPS".equalsIgnoreCase(sort)) {
            return Sort.by(Sort.Direction.DESC, "scrapCount").and(Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        return Sort.by(Sort.Direction.DESC, "createdAt");
    }
}

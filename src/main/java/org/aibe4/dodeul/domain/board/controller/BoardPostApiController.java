package org.aibe4.dodeul.domain.board.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.BoardPostListPageResponse;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostCreateRequest;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostListRequest;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostCreateResponse;
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
    @GetMapping
    public CommonResponse<BoardPostListPageResponse> listPosts(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(value = "consultingTag", required = false) ConsultingTag consultingTag,
        @RequestParam(value = "tagIds", required = false) List<Long> tagIds,
        @RequestParam(value = "status", required = false) String status,
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "sort", required = false) String sort,
        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {

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

    /**
     * 게시글 작성
     */
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
            return Sort.by(Sort.Direction.DESC, "likeCount")
                .and(Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        if ("SCRAPS".equalsIgnoreCase(sort)) {
            return Sort.by(Sort.Direction.DESC, "scrapCount")
                .and(Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        return Sort.by(Sort.Direction.DESC, "createdAt");
    }
}

// src/main/java/org/aibe4/dodeul/domain/board/controller/BoardPostApiController.java
package org.aibe4.dodeul.domain.board.controller;

import java.util.List;
import org.aibe4.dodeul.domain.board.model.dto.BoardPostListPageResponse;
import org.aibe4.dodeul.domain.board.model.dto.BoardPostListRequest;
import org.aibe4.dodeul.domain.board.model.dto.BoardPostListResponse;
import org.aibe4.dodeul.domain.board.service.BoardPostService;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.aibe4.dodeul.global.response.ApiResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board/posts")
public class BoardPostApiController {

    private final BoardPostService boardPostService;

    public BoardPostApiController(BoardPostService boardPostService) {
        this.boardPostService = boardPostService;
    }

    /**
     * 게시글 목록 조회
     *
     * <p>임시 규칙: Authorization 헤더가 "Bearer {memberId}" 형태면 memberId로 간주한다.
     */
    @GetMapping
    public ApiResponse<BoardPostListPageResponse> listPosts(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "consultingTag", required = false) ConsultingTag consultingTag,
            @RequestParam(value = "tagIds", required = false) List<Long> tagIds,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "sort", required = false, defaultValue = "LATEST") String sort,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {

        Long memberId = parseMemberIdFromAuthorization(authorization);

        BoardPostListRequest request =
                BoardPostListRequest.builder()
                        .consultingTag(consultingTag)
                        .skillTagIds(tagIds)
                        .status(status)
                        .keyword(keyword)
                        .sort(sort)
                        .build();

        Pageable pageable = PageRequest.of(page, size, toSpringSort(sort));
        Page<BoardPostListResponse> result = boardPostService.getPosts(request, memberId, pageable);

        BoardPostListPageResponse data = BoardPostListPageResponse.from(result);
        return ApiResponse.success(SuccessCode.SUCCESS, data, "게시글 목록 조회 성공");
    }

    private Sort toSpringSort(String sort) {
        if (sort == null || sort.isBlank() || "LATEST".equalsIgnoreCase(sort)) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        if ("VIEWS".equalsIgnoreCase(sort)) {
            return Sort.by(Sort.Direction.DESC, "viewCount")
                    .and(Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        if ("ACTIVE".equalsIgnoreCase(sort)) {
            return Sort.by(Sort.Direction.DESC, "lastCommentedAt")
                    .and(Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        return Sort.by(Sort.Direction.DESC, "createdAt");
    }

    private Long parseMemberIdFromAuthorization(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }

        String token = authorization.substring("Bearer ".length()).trim();
        try {
            return Long.parseLong(token);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

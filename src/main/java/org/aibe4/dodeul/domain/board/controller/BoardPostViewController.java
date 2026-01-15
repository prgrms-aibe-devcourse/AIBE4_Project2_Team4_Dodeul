// src/main/java/org/aibe4/dodeul/domain/board/controller/BoardPostViewController.java
package org.aibe4.dodeul.domain.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.comment.BoardCommentListResponse;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostCreateRequest;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostFileCreateRequest;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostListRequest;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostDetailResponse;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostListResponse;
import org.aibe4.dodeul.domain.board.service.*;
import org.aibe4.dodeul.domain.common.repository.SkillTagRepository;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.aibe4.dodeul.global.file.model.dto.response.FileUploadResponse;
import org.aibe4.dodeul.global.file.service.FileService;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardPostViewController {

    private final BoardPostService boardPostService;
    private final BoardPostDetailService boardPostDetailService;
    private final BoardCommentService boardCommentService;
    private final BoardPostScrapService boardPostScrapService;
    private final SkillTagRepository skillTagRepository;
    private final FileService fileService;
    private final BoardPostFileService boardPostFileService;

    @GetMapping("/board")
    public String boardHome() {
        return "redirect:/board/posts";
    }

    /**
     * 게시글 목록 페이지 (Thymeleaf SSR)
     */
    @GetMapping("/board/posts")
    public String listPage(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String sort,
        @RequestParam(required = false) List<Long> tagIds,
        @RequestParam(required = false) ConsultingTag consultingTag,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "12") int size,
        Model model) {

        Long memberId = userDetails == null ? null : userDetails.getMemberId();

        BoardPostListRequest request = BoardPostListRequest.builder()
            .consultingTag(consultingTag)
            .tagIds(tagIds)
            .status(status)
            .keyword(keyword)
            .sort(sort)
            .build();

        Pageable pageable = PageRequest.of(page, size, toSpringSort(sort));

        try {
            Page<BoardPostListResponse> result = boardPostService.getPosts(request, memberId, pageable);

            // Model에 데이터 추가
            model.addAttribute("posts", result.getContent());
            model.addAttribute("currentPage", result.getNumber());
            model.addAttribute("totalPages", result.getTotalPages());
            model.addAttribute("pageSize", size);
            model.addAttribute("skillTags", skillTagRepository.findAll());

        } catch (Exception e) {
            // 로그인 정책 위반 등으로 실패 시 빈 목록
            model.addAttribute("posts", List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("pageSize", size);
            model.addAttribute("skillTags", skillTagRepository.findAll());
        }

        return "board/post-list";
    }

    /**
     * 게시글 상세 페이지 (Thymeleaf SSR)
     */
    @GetMapping("/board/posts/{postId}")
    public String detail(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Long postId,
        Model model) {

        Long memberId = userDetails == null ? null : userDetails.getMemberId();

        // 조회수 증가
        boardPostService.increaseViewCount(postId);

        // 게시글 상세 정보
        BoardPostDetailResponse post = boardPostDetailService.getDetail(postId, memberId);

        // 댓글 목록
        BoardCommentListResponse commentsResponse = boardCommentService.getComments(postId, memberId);

        // 스크랩 여부
        boolean isScraped = false;
        if (memberId != null) {
            try {
                isScraped = boardPostScrapService.getStatus(memberId, postId).isScrappedByMe();
            } catch (Exception e) {
                // 무시
            }
        }

        // Model에 데이터 추가
        model.addAttribute("post", post);
        model.addAttribute("comments", commentsResponse.getComments());
        model.addAttribute("isScraped", isScraped);
        model.addAttribute("isAuthor", post.mine());  // Response의 mine 필드 사용
        model.addAttribute("isAuthenticated", memberId != null);

        return "board/post-detail";
    }

    @GetMapping("/board/posts/new")
    public String createForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        if (memberId == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("form", new BoardPostCreateRequest());
        model.addAttribute("consultingTags", ConsultingTag.values());
        model.addAttribute("skillTags", skillTagRepository.findAll());
        model.addAttribute("skillTagIdString", "");
        return "board/post-form";
    }

    @PostMapping("/board/posts")
    public String create(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @ModelAttribute("form") BoardPostCreateRequest form,
        BindingResult bindingResult,
        @ModelAttribute("skillTagIdString") String skillTagIdString,
        @RequestParam(value = "files", required = false) List<MultipartFile> files,
        Model model,
        RedirectAttributes rttr) {

        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        if (memberId == null) {
            rttr.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/auth/login";
        }

        applySkillTagsIfNeeded(form, skillTagIdString);

        if (bindingResult.hasErrors()) {
            model.addAttribute("consultingTags", ConsultingTag.values());
            model.addAttribute("skillTags", skillTagRepository.findAll());
            model.addAttribute("skillTagIdString", skillTagIdString == null ? "" : skillTagIdString);
            return "board/post-form";
        }

        try {
            // 게시글 생성
            Long postId = boardPostService.createPost(memberId, form);

            // 파일 업로드 및 저장
            if (files != null && !files.isEmpty()) {
                List<BoardPostFileCreateRequest.Item> fileItems = new ArrayList<>();

                for (MultipartFile file : files) {
                    if (file == null || file.isEmpty()) {
                        continue;
                    }

                    FileUploadResponse uploaded = fileService.upload(file, "board");

                    fileItems.add(BoardPostFileCreateRequest.Item.of(
                        uploaded.getFileUrl(),
                        uploaded.getOriginFileName(),
                        uploaded.getContentType(),
                        uploaded.getFileSize()
                    ));
                }

                if (!fileItems.isEmpty()) {
                    BoardPostFileCreateRequest fileRequest = BoardPostFileCreateRequest.of(fileItems);
                    boardPostFileService.addFiles(memberId, postId, fileRequest);
                }
            }

            rttr.addFlashAttribute("msg", "게시글이 등록되었습니다.");
            return "redirect:/board/posts/" + postId;

        } catch (IllegalArgumentException | IllegalStateException e) {
            bindingResult.reject("create.fail", e.getMessage());
            model.addAttribute("consultingTags", ConsultingTag.values());
            model.addAttribute("skillTags", skillTagRepository.findAll());
            model.addAttribute("skillTagIdString", skillTagIdString == null ? "" : skillTagIdString);
            return "board/post-form";
        }
    }

    @GetMapping("/board/posts/{postId}/edit")
    public String editForm(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Long postId,
        Model model,
        RedirectAttributes rttr) {

        Long memberId = userDetails == null ? null : userDetails.getMemberId();
        if (memberId == null) {
            rttr.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/auth/login";
        }

        model.addAttribute("postId", postId);
        model.addAttribute("consultingTags", ConsultingTag.values());
        model.addAttribute("skillTags", skillTagRepository.findAll());
        model.addAttribute("skillTagIdString", "");

        return "board/post-edit";
    }

    private void applySkillTagsIfNeeded(BoardPostCreateRequest form, String skillTagIdString) {
        if (skillTagIdString == null || skillTagIdString.isBlank()) {
            return;
        }

        List<String> parsedNames =
            Arrays.stream(skillTagIdString.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .filter(token -> !token.matches("\\d+"))
                .map(this::normalizeForName)
                .distinct()
                .toList();

        List<Long> parsedIds =
            Arrays.stream(skillTagIdString.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .filter(token -> token.matches("\\d+"))
                .map(Long::valueOf)
                .distinct()
                .toList();

        if (parsedIds != null && !parsedIds.isEmpty()) {
            List<Long> merged =
                new java.util.ArrayList<>(form.getSkillTagIds() == null ? List.of() : form.getSkillTagIds());
            merged.addAll(parsedIds);
            form.setSkillTagIds(merged.stream().distinct().toList());
        }

        if (parsedNames != null && !parsedNames.isEmpty()) {
            List<String> merged =
                new java.util.ArrayList<>(
                    form.getSkillTagNames() == null ? List.of() : form.getSkillTagNames());
            merged.addAll(parsedNames);
            form.setSkillTagNames(merged.stream().distinct().toList());
        }
    }

    private String normalizeForName(String value) {
        return value == null ? "" : value.trim();
    }

    private Sort toSpringSort(String sort) {
        if (sort == null || sort.isBlank() || "LATEST".equalsIgnoreCase(sort)) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        if ("VIEWS".equalsIgnoreCase(sort)) {
            return Sort.by(Sort.Direction.DESC, "viewCount").and(Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        if ("SCRAPS".equalsIgnoreCase(sort)) {
            return Sort.by(Sort.Direction.DESC, "scrapCount").and(Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        return Sort.by(Sort.Direction.DESC, "createdAt");
    }
}

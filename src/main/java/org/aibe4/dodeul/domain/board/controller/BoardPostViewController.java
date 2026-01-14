// src/main/java/org/aibe4/dodeul/domain/board/controller/BoardPostViewController.java
package org.aibe4.dodeul.domain.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostCreateRequest;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostFileCreateRequest;
import org.aibe4.dodeul.domain.board.service.BoardPostFileService;
import org.aibe4.dodeul.domain.board.service.BoardPostService;
import org.aibe4.dodeul.domain.common.repository.SkillTagRepository;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.aibe4.dodeul.global.file.model.dto.response.FileUploadResponse;
import org.aibe4.dodeul.global.file.service.FileService;
import org.aibe4.dodeul.global.security.CustomUserDetails;
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
    private final SkillTagRepository skillTagRepository;
    private final FileService fileService;
    private final BoardPostFileService boardPostFileService;  // ← 추가!

    @GetMapping("/board")
    public String boardHome() {
        return "redirect:/board/posts";
    }

    @GetMapping("/board/posts")
    public String listPage() {
        return "board/post-list";
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
            // 1. 게시글 먼저 생성
            Long postId = boardPostService.createPost(memberId, form);

            // 2. 파일이 있으면 업로드 후 CommonFile로 저장
            if (files != null && !files.isEmpty()) {
                List<BoardPostFileCreateRequest.Item> fileItems = new ArrayList<>();

                for (MultipartFile file : files) {
                    if (file == null || file.isEmpty()) {
                        continue;
                    }

                    // Supabase에 업로드
                    FileUploadResponse uploaded = fileService.upload(file, "board");

                    // CommonFile 저장을 위한 Item 생성
                    fileItems.add(BoardPostFileCreateRequest.Item.of(
                        uploaded.getFileUrl(),
                        uploaded.getOriginFileName(),
                        uploaded.getContentType(),
                        uploaded.getFileSize()
                    ));
                }

                // CommonFile 테이블에 저장
                if (!fileItems.isEmpty()) {
                    BoardPostFileCreateRequest fileRequest =
                        BoardPostFileCreateRequest.of(fileItems);
                    // ✅ 수정: static 호출 → 인스턴스 호출
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

    @GetMapping("/board/posts/{postId}")
    public String detail(@PathVariable Long postId, Model model) {
        model.addAttribute("postId", postId);
        return "board/post-detail";
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
}

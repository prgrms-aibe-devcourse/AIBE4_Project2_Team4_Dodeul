package org.aibe4.dodeul.domain.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostCreateRequest;
import org.aibe4.dodeul.domain.board.service.BoardPostService;
import org.aibe4.dodeul.domain.common.repository.SkillTagRepository;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardPostViewController {

    private final BoardPostService boardPostService;
    private final SkillTagRepository skillTagRepository;

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
            Long postId = boardPostService.createPost(memberId, form);
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
                new java.util.ArrayList<>(
                    form.getSkillTagIds() == null ? List.of() : form.getSkillTagIds());
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

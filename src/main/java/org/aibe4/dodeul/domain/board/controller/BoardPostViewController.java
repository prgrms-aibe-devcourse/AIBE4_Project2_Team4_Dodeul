package org.aibe4.dodeul.domain.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostCreateRequest;
import org.aibe4.dodeul.domain.board.service.BoardPostService;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;
import org.aibe4.dodeul.domain.common.repository.SkillTagRepository;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board/posts")
public class BoardPostViewController {

    private final BoardPostService boardPostService;
    private final SkillTagRepository skillTagRepository;

    @GetMapping("/new")
    public String createForm(@AuthenticationPrincipal Long memberId, Model model) {
        if (memberId == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("form", new BoardPostCreateRequest());
        model.addAttribute("consultingTags", ConsultingTag.values());
        model.addAttribute("skillTags", skillTagRepository.findAll());
        model.addAttribute("skillTagIdString", "");
        return "board/post-form";
    }

    @PostMapping
    public String create(
        @AuthenticationPrincipal Long memberId,
        @Valid @ModelAttribute("form") BoardPostCreateRequest form,
        BindingResult bindingResult,
        @ModelAttribute("skillTagIdString") String skillTagIdString,
        Model model,
        RedirectAttributes rttr) {

        if (memberId == null) {
            rttr.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/auth/login";
        }

        applySkillTagsIfNeeded(form, skillTagIdString, bindingResult);

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

    @GetMapping("/{postId}")
    public String detail(@PathVariable Long postId, Model model) {
        model.addAttribute("postId", postId);
        return "board/post-detail";
    }

    private void applySkillTagsIfNeeded(
        BoardPostCreateRequest form, String skillTagIdString, BindingResult bindingResult) {
        if (form.getSkillTagIds() != null && !form.getSkillTagIds().isEmpty()) {
            return;
        }
        if (skillTagIdString == null || skillTagIdString.isBlank()) {
            return;
        }

        Map<String, Long> nameToId =
            skillTagRepository.findAll().stream()
                .collect(
                    Collectors.toMap(
                        t -> normalize(t.getName()),
                        SkillTag::getId,
                        (a, b) -> a)); // 중복 이름은 첫 번째 우선

        List<Long> parsed =
            Arrays.stream(skillTagIdString.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(
                    token -> {
                        if (token.matches("\\d+")) {
                            return Long.valueOf(token);
                        }
                        Long id = nameToId.get(normalize(token));
                        if (id == null) {
                            bindingResult.rejectValue(
                                "skillTagIds", "skillTag.invalid", "존재하지 않는 스킬태그가 포함되어 있습니다.");
                        }
                        return id;
                    })
                .filter(v -> v != null)
                .distinct()
                .toList();

        form.setSkillTagIds(parsed);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}

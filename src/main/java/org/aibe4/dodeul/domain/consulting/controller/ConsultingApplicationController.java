package org.aibe4.dodeul.domain.consulting.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationDetailResponse;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationRequest;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.aibe4.dodeul.domain.consulting.service.ConsultingApplicationService;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/consulting-applications")
@RequiredArgsConstructor
public class ConsultingApplicationController {

    private final ConsultingApplicationService consultingApplicationService;

    // 1. 작성 폼
    @GetMapping("/form")
    public String applicationForm(Model model) {
        model.addAttribute("request", new ConsultingApplicationRequest());
        model.addAttribute("consultingTags", ConsultingTag.values());
        model.addAttribute("formActionUrl", "/consulting-applications");
        return "consulting/application-form";
    }

    // 2. 등록 처리 (✅ 깔끔해진 버전)
    @PostMapping
    public String registerApplication(
        @Valid @ModelAttribute("request") ConsultingApplicationRequest request,
        BindingResult bindingResult,
        Model model,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        // 유효성 검사 실패 시 다시 폼으로
        if (bindingResult.hasErrors()) {
            model.addAttribute("consultingTags", ConsultingTag.values());
            model.addAttribute("formActionUrl", "/consulting-applications");
            return "consulting/application-form";
        }

        // 로그인한 사용자 ID 설정 (user가 null이면 에러가 나는 게 맞음)
        request.setMenteeId(user.getMemberId());

        // 저장 (try-catch 제거 -> 에러 나면 스프링이 알아서 처리)
        Long savedApplicationId = consultingApplicationService.saveApplication(request);

        // ★ 성공 후 HTML 메시지 대신 '상세 페이지'로 바로 이동 (Redirect)
        return "redirect:/consulting-applications/" + savedApplicationId;
    }

    // 3. 상세 조회
    @GetMapping("/{applicationId}")
    public String getApplicationDetail(@PathVariable Long applicationId, Model model) {
        ConsultingApplicationDetailResponse response =
            consultingApplicationService.getApplicationDetail(applicationId);
        model.addAttribute("application", response);
        return "consulting/application-detail";
    }

    // 4. 수정 폼
    @GetMapping("/{applicationId}/edit")
    public String editForm(
        @PathVariable Long applicationId,
        Model model,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        ConsultingApplication application = consultingApplicationService.findApplicationEntity(applicationId);

        // (필요 시) 본인 확인 로직 추가 가능
        // if (!application.getMenteeId().equals(user.getMemberId())) { ... }

        ConsultingApplicationRequest form = new ConsultingApplicationRequest();
        form.setTitle(application.getTitle());
        form.setContent(application.getContent());
        form.setConsultingTag(application.getConsultingTag());
        form.setFileUrl(application.getFileUrl());

        // Entity -> DTO 매핑 (SkillTag)
        String tags = application.getApplicationSkillTags().stream()
            .map(tag -> tag.getSkillTag().getName())
            .collect(Collectors.joining(", "));
        form.setTechTags(tags);

        model.addAttribute("request", form);
        model.addAttribute("consultingTags", ConsultingTag.values());
        model.addAttribute("formActionUrl", "/consulting-applications/" + applicationId + "/edit");
        return "consulting/application-form";
    }

    // 5. 수정 처리
    @PostMapping("/{applicationId}/edit")
    public String updateApplication(
        @PathVariable Long applicationId,
        @Valid @ModelAttribute("request") ConsultingApplicationRequest request,
        BindingResult bindingResult,
        Model model,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("consultingTags", ConsultingTag.values());
            model.addAttribute("formActionUrl", "/consulting-applications/" + applicationId + "/edit");
            return "consulting/application-form";
        }

        // 로그인한 유저 ID 사용
        consultingApplicationService.updateApplication(applicationId, user.getMemberId(), request);

        return "redirect:/consulting-applications/" + applicationId;
    }

    // 6. 삭제 처리
    @PostMapping("/{applicationId}/delete")
    public String deleteApplication(
        @PathVariable Long applicationId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        // 로그인한 유저 ID 사용
        consultingApplicationService.deleteApplication(applicationId, user.getMemberId());
        return "redirect:/";
    }
}

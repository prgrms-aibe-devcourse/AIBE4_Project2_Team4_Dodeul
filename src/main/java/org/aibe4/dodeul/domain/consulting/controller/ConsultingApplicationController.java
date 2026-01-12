package org.aibe4.dodeul.domain.consulting.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationDetailResponse;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationRequest;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.aibe4.dodeul.domain.consulting.service.ConsultingApplicationService;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    // 2. 등록 처리
    @PostMapping
    public String registerApplication(
        @Valid @ModelAttribute("request") ConsultingApplicationRequest request,
        BindingResult bindingResult,
        Model model,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("consultingTags", ConsultingTag.values());
            model.addAttribute("formActionUrl", "/consulting-applications");
            return "consulting/application-form";
        }

        request.setMenteeId(user.getMemberId());
        Long savedApplicationId = consultingApplicationService.saveApplication(request);

        return "redirect:/matchings/new?applicationId=" + savedApplicationId;
    }

    // 3. 상세 조회
    @GetMapping("/{applicationId}")
    public String getApplicationDetail(@PathVariable Long applicationId, Model model) {
        ConsultingApplicationDetailResponse response =
            consultingApplicationService.getApplicationDetail(applicationId);
        model.addAttribute("appDetail", response);
        return "consulting/application-detail";
    }

    // 4. 수정 폼 (✅ 서비스 로직 분리로 매우 깔끔해진 부분!)
    @GetMapping("/{applicationId}/edit")
    public String editForm(@PathVariable Long applicationId, Model model) {

        // [수정 포인트] 복잡한 가공 로직을 서비스 메서드 하나로 대체
        ConsultingApplicationRequest form = consultingApplicationService.getRegistrationForm(applicationId);

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

        consultingApplicationService.updateApplication(applicationId, user.getMemberId(), request);

        return "redirect:/consulting-applications/" + applicationId;
    }

    // 6. 삭제 처리
    @PostMapping("/{applicationId}/delete")
    public String deleteApplication(
        @PathVariable Long applicationId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        consultingApplicationService.deleteApplication(applicationId, user.getMemberId());
        return "redirect:/";
    }
}

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
        // 기본 유효성 검사 (빈칸 등)
        if (bindingResult.hasErrors()) {
            model.addAttribute("consultingTags", ConsultingTag.values());
            model.addAttribute("formActionUrl", "/consulting-applications");
            return "consulting/application-form";
        }

        try {
            // 정상적인 저장 시도
            request.setMenteeId(user.getMemberId());
            Long savedApplicationId = consultingApplicationService.saveApplication(request);

            return "redirect:/matchings/new?applicationId=" + savedApplicationId;

        } catch (IllegalArgumentException e) {
            // [추가된 부분] 비속어/정책 위반 예외 발생 시 처리

            // 1. 에러 메시지 전달 (HTML에서 alert로 띄움)
            model.addAttribute("errorMessage", e.getMessage());

            // 2. 화면 구성을 위한 기본 데이터 다시 세팅
            model.addAttribute("consultingTags", ConsultingTag.values());
            model.addAttribute("formActionUrl", "/consulting-applications");

            // 3. 입력했던 내용은 'request' 객체에 남아있으므로 그대로 폼으로 복귀
            return "consulting/application-form";
        }
    }

    // 3. 상세 조회
    @GetMapping("/{applicationId}")
    public String getApplicationDetail(@PathVariable Long applicationId, Model model) {
        ConsultingApplicationDetailResponse response =
            consultingApplicationService.getApplicationDetail(applicationId);
        model.addAttribute("appDetail", response);
        return "consulting/application-detail";
    }

    // 4. 수정 폼
    @GetMapping("/{applicationId}/edit")
    public String editForm(
        @PathVariable Long applicationId,
        Model model,
        @AuthenticationPrincipal CustomUserDetails user // [수정] 이 부분이 추가되어야 빨간 줄이 사라집니다!
    ) {
        // 서비스에서 수정용 폼 데이터 가져오기
        ConsultingApplicationRequest form = consultingApplicationService.getRegistrationForm(applicationId);

        // [보안 검사] 이제 user 변수를 사용할 수 있습니다.
        if (!form.getMenteeId().equals(user.getMemberId())) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

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

package org.aibe4.dodeul.domain.consulting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationRequest;
import org.aibe4.dodeul.domain.consulting.service.ConsultingApplicationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Consulting Application", description = "상담 신청 관련 컨트롤러")
@Controller
@RequestMapping("/consulting-applications")
@RequiredArgsConstructor
public class ConsultingApplicationController {

    private final ConsultingApplicationService consultingApplicationService;

    @Operation(summary = "상담 신청 등록", description = "멘티가 상담 신청서를 작성하여 등록합니다.")
    @PostMapping
    public String registerApplication(
        @ModelAttribute ConsultingApplicationRequest request,
        @AuthenticationPrincipal UserDetails user
    ) {

        if (user != null) {
            Long currentUserId = Long.parseLong(user.getUsername());
            request.setMenteeId(currentUserId);
        }

        // 1. 서비스 호출 (저장)
        Long savedApplicationId = consultingApplicationService.saveApplication(request);

        // 2. 리다이렉션
        return "redirect:/matching/recommend?applicationId=" + savedApplicationId;
    }
}

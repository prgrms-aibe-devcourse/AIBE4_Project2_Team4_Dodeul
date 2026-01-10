package org.aibe4.dodeul.domain.consulting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationDetailResponse;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationRequest;
import org.aibe4.dodeul.domain.consulting.service.ConsultingApplicationService;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        @AuthenticationPrincipal CustomUserDetails user
    ) {

        if (user != null) {
            Long currentUserId = user.getMemberId();
            request.setMenteeId(currentUserId);
        }

        // 1. 서비스 호출 (저장)
        Long savedApplicationId = consultingApplicationService.saveApplication(request);

        // 2. 리다이렉션
        return "redirect:/matchings/new?applicationId=" + savedApplicationId;
    }

    @Operation(summary = "상담 신청 상세 조회", description = "상담 신청서의 상세 내용을 조회합니다.")
    @GetMapping("/{applicationId}")
    public String getApplicationDetail(@PathVariable Long applicationId, Model model) {

        // 1. 서비스에서 상세 내용 가져오기
        ConsultingApplicationDetailResponse response =
            consultingApplicationService.getApplicationDetail(applicationId);

        // 2. HTML에 데이터 전달
        model.addAttribute("application", response);

        // 3. HTML 파일 보여주기
        return "consulting/application-detail";
    }
}

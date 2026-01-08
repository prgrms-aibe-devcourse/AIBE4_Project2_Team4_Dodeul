package org.aibe4.dodeul.domain.consulting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationDetailResponse;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationRequest;
import org.aibe4.dodeul.domain.consulting.service.ConsultingApplicationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
            @AuthenticationPrincipal UserDetails user) {

        if (user != null) {
            Long currentUserId = Long.parseLong(user.getUsername());
            request.setMenteeId(currentUserId);
        }

        Long savedApplicationId = consultingApplicationService.saveApplication(request);

        return "redirect:/matching/recommend?applicationId=" + savedApplicationId;
    }

    @Operation(summary = "상담 신청 상세 조회", description = "상담 신청서의 상세 내용을 조회합니다.")
    @GetMapping("/{applicationId}")
    public String getApplicationDetail(@PathVariable Long applicationId, Model model) {
        // 1. 서비스 호출 (상세 정보 가져오기)
        ConsultingApplicationDetailResponse response =
                consultingApplicationService.getApplicationDetail(applicationId);

        // 2. 모델에 데이터 담기 (뷰에서 'application'이라는 이름으로 사용 가능)
        model.addAttribute("application", response);

        // 3. 뷰 이름 반환 (src/main/resources/templates/consulting/application-detail.html 파일을 찾는다고 가정)
        // 파일 경로는 실제 생성하실 html 경로에 맞춰 수정해주세요.
        return "consulting/application-detail";
    }
}

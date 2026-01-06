package org.aibe4.dodeul.domain.consulting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consulting.dto.ConsultingApplicationRequest;
import org.aibe4.dodeul.domain.consulting.service.ConsultingApplicationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Consulting Application", description = "상담 신청 관련 컨트롤러")
@Controller
@RequestMapping("/consulting-applications") // 맨 앞에 슬래시(/) 붙이는 것이 관례상 좋습니다.
@RequiredArgsConstructor
public class ConsultingApplicationController {

    private final ConsultingApplicationService consultingApplicationService;

    @Operation(summary = "상담 신청 등록", description = "멘티가 상담 신청서를 작성하여 등록합니다.")
    @PostMapping
    public String registerApplication(@ModelAttribute ConsultingApplicationRequest request) {

        // 1. 서비스 호출 (저장)
        consultingApplicationService.saveApplication(request);

        // 2. 리다이렉션 (작업 완료 후 목록 페이지나 메인으로 이동)
        // "redirect:/" 뒤에는 이동하고 싶은 URL 경로를 적습니다.
        return "redirect:/consulting-applications";
    }
}

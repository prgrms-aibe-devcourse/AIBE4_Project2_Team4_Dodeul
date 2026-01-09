package org.aibe4.dodeul.domain.consulting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationDetailResponse;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationRequest;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.aibe4.dodeul.domain.consulting.service.ConsultingApplicationService;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Consulting Application", description = "상담 신청 관련 컨트롤러")
@Controller
@RequestMapping("/consulting-applications")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()") // [추가] 로그인한 사람만 이 컨트롤러 기능 사용 가능
public class ConsultingApplicationController {

    private final ConsultingApplicationService consultingApplicationService;

    @Operation(summary = "상담 신청 폼 이동", description = "상담 신청서 작성 화면을 보여줍니다.")
    @GetMapping("/form")
    public String applicationForm(Model model) {
        model.addAttribute("request", new ConsultingApplicationRequest());
        model.addAttribute("consultingTags", ConsultingTag.values());
        return "consulting/application-form";
    }

    @Operation(summary = "상담 신청 등록", description = "멘티가 상담 신청서를 작성하여 등록합니다.")
    @PostMapping
    public String registerApplication(
        @Valid @ModelAttribute("request") ConsultingApplicationRequest request, // [수정] @Valid 추가
        BindingResult bindingResult, // [수정] 에러를 잡아내는 객체 추가
        Model model,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        // 1. 유효성 검사 실패 시 (예: 제목을 안 씀) 다시 작성 폼으로 돌려보냄
        if (bindingResult.hasErrors()) {
            // 카테고리 목록이 없으면 에러 나므로 다시 담아줘야 함
            model.addAttribute("consultingTags", ConsultingTag.values());
            return "consulting/application-form";
        }

        // 2. 로그인 사용자 ID 설정
        if (user != null) {
            request.setMenteeId(user.getMemberId());
        }

        // 3. 서비스 호출 (저장)
        Long savedApplicationId = consultingApplicationService.saveApplication(request);

        // 4. 리다이렉션 (팀원분이 만든 매칭 생성 URL로 이동)
        return "redirect:/matchings/new?applicationId=" + savedApplicationId;
    }

    @Operation(summary = "상담 신청 상세 조회", description = "상담 신청서의 상세 내용을 조회합니다.")
    @GetMapping("/{applicationId}")
    public String getApplicationDetail(@PathVariable Long applicationId, Model model) {

        // 1. 서비스에서 상세 내용 가져오기 (팀원이 만든 DTO 반환)
        ConsultingApplicationDetailResponse response =
            consultingApplicationService.getApplicationDetail(applicationId);

        // 2. HTML에 데이터 전달
        model.addAttribute("application", response);

        // 3. HTML 파일 보여주기
        return "consulting/application-detail";
    }
}

package org.aibe4.dodeul.domain.consulting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationDetailResponse;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationRequest;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.aibe4.dodeul.domain.consulting.service.ConsultingApplicationService;
import org.aibe4.dodeul.domain.member.service.MemberService;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/consulting-applications")
@RequiredArgsConstructor
@Tag(name = "Consulting", description = "상담 신청 화면(HTML) 관련 Controller")
public class ConsultingApplicationController {

    private final ConsultingApplicationService consultingApplicationService;
    private final MemberService memberService;

    // 1. 작성 폼
    @Operation(summary = "상담 신청 폼 이동", description = "상담 신청서를 작성하는 페이지를 반환합니다.")
    @GetMapping("/form")
    public String applicationForm(
        @RequestParam(value = "mentorId", required = false) Long mentorId,
        Model model
    ) {
        if (mentorId != null) {
            model.addAttribute("mentorId", mentorId);
            String nickname = memberService.getMemberOrThrow(mentorId).getNickname();
            model.addAttribute("mentorName", nickname);
        }
        model.addAttribute("request", new ConsultingApplicationRequest());
        model.addAttribute("consultingTags", ConsultingTag.values());
        model.addAttribute("formActionUrl", "/consulting-applications");
        return "consulting/application-form";
    }

    // 2. 등록 처리
    @Operation(summary = "상담 신청 등록 처리", description = "작성한 폼 데이터를 받아 DB에 저장하고 매칭 대기 화면으로 이동합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "등록 성공 (화면 이동)"),
        @ApiResponse(responseCode = "400", description = "잘못된 입력값 (제목 누락, 비속어 등)"),
        @ApiResponse(responseCode = "401", description = "로그인 필요")
    })
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
            // 비속어/정책 위반 예외 발생 시 처리
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("consultingTags", ConsultingTag.values());
            model.addAttribute("formActionUrl", "/consulting-applications");
            return "consulting/application-form";
        }
    }

    // 3. 상세 조회
    @Operation(summary = "상담 신청 상세 조회 (멘티용)", description = "신청한 상담의 상세 내용을 보여줍니다.")
    @GetMapping("/{applicationId}")
    public String getApplicationDetail(@PathVariable Long applicationId, Model model) {
        ConsultingApplicationDetailResponse response =
            consultingApplicationService.getApplicationDetail(applicationId);
        model.addAttribute("appDetail", response);
        return "consulting/application-detail";
    }

    // [누락 주의] 아까 만든 멘토 전용 상세 조회
    @Operation(summary = "상담 신청 상세 조회 (멘토용)", description = "멘토가 요청받은 상담 내용을 확인합니다.")
    @GetMapping("/{applicationId}/mentor-view")
    public String viewMentorApplicationDetail(@PathVariable Long applicationId, Model model) {
        ConsultingApplicationDetailResponse response =
            consultingApplicationService.getApplicationDetail(applicationId);

        model.addAttribute("appDetail", response);
        return "consulting/application-mento-detail";
    }

    // 4. 수정 폼
    @Operation(summary = "상담 신청 수정 폼 이동", description = "기존에 작성한 내용을 수정하는 페이지로 이동합니다.")
    @GetMapping("/{applicationId}/edit")
    public String editForm(
        @PathVariable Long applicationId,
        Model model,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        // 1. 서비스에서 수정용 데이터 가져오기
        ConsultingApplicationRequest form = consultingApplicationService.getRegistrationForm(applicationId);

        // 2. 권한 검사 (Null 방지 추가)
        if (form == null || user == null || !form.getMenteeId().equals(user.getMemberId())) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        // 3. HTML 렌더링 에러 방지
        model.addAttribute("mentorId", null);
        model.addAttribute("mentorName", "");

        model.addAttribute("request", form);
        model.addAttribute("consultingTags", ConsultingTag.values());
        model.addAttribute("formActionUrl", "/consulting-applications/" + applicationId + "/edit");

        return "consulting/application-form";
    }

    // 5. 수정 처리
    @Operation(summary = "상담 신청 수정 처리", description = "수정된 내용을 DB에 반영합니다.")
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
    @Operation(summary = "상담 신청 삭제", description = "상담 신청서를 삭제합니다.")
    @PostMapping("/{applicationId}/delete")
    public String deleteApplication(
        @PathVariable Long applicationId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        consultingApplicationService.deleteApplication(applicationId, user.getMemberId());
        return "redirect:/";
    }

    @Operation(summary = "매칭 ID로 상세 조회 (멘티용)", description = "매칭 정보를 통해 신청서 상세 내용을 조회합니다.")
    @GetMapping("/matching/{matchingId}")
    public String getApplicationDetailByMatching(
        @PathVariable Long matchingId,
        @AuthenticationPrincipal CustomUserDetails user,
        Model model
    ) {
        if (user == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        Long applicationId =
            consultingApplicationService.findApplicationIdByMatchingForMentee(
                matchingId,
                user.getMemberId()
            );

        ConsultingApplicationDetailResponse response =
            consultingApplicationService.getApplicationDetail(applicationId);

        model.addAttribute("appDetail", response);
        return "consulting/application-detail";
    }

    @Operation(summary = "매칭 ID로 상세 조회 (멘토용)", description = "매칭 정보를 통해 신청서 상세 내용을 조회합니다.")
    @GetMapping("/matching/{matchingId}/mentor-view")
    public String getApplicationDetailByMatchingForMentor(
        @PathVariable Long matchingId,
        @AuthenticationPrincipal CustomUserDetails user,
        Model model
    ) {
        if (user == null) throw new IllegalStateException("로그인이 필요합니다.");

        Long applicationId =
            consultingApplicationService.findApplicationIdByMatchingForMentor(
                matchingId,
                user.getMemberId()
            );

        ConsultingApplicationDetailResponse response =
            consultingApplicationService.getApplicationDetail(applicationId);

        model.addAttribute("appDetail", response);
        return "consulting/application-mento-detail";
    }
}

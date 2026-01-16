package org.aibe4.dodeul.domain.consulting.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationRequest;
import org.aibe4.dodeul.domain.consulting.service.ConsultingApplicationService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/consulting-applications")
@RequiredArgsConstructor
@Tag(name = "Consulting", description = "상담 관련 API (AI 초안 및 신청서)") // 이 줄을 추가하세요
public class ConsultingApplicationApiController {

    private final ConsultingApplicationService consultingApplicationService;

    @PostMapping
    @PreAuthorize("hasRole('MENTEE')")
    public CommonResponse<Long> saveApplicationApi(
        @Valid @ModelAttribute ConsultingApplicationRequest request,
        BindingResult bindingResult,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError() != null
                ? bindingResult.getFieldError().getDefaultMessage()
                : "입력값이 올바르지 않습니다.";
            return CommonResponse.fail(ErrorCode.INVALID_INPUT_VALUE, errorMessage);
        }

        try {
            request.setMenteeId(user.getMemberId());
            Long savedId = consultingApplicationService.saveApplication(request);

            return CommonResponse.success(SuccessCode.SUCCESS, savedId);

        } catch (IllegalArgumentException e) {
            return CommonResponse.fail(ErrorCode.INVALID_INPUT_VALUE, e.getMessage());
        } catch (Exception e) {
            log.error("신청서 저장 오류", e);
            return CommonResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}

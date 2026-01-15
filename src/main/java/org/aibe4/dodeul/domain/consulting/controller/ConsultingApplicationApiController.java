package org.aibe4.dodeul.domain.consulting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Consulting-Page", description = "상담 신청 처리 REST API") // ✅ 그룹 이름 명시
public class ConsultingApplicationApiController {

    private final ConsultingApplicationService consultingApplicationService;

    @Operation(summary = "상담 신청 등록 (API)", description = "멘티가 작성한 상담 신청서를 DB에 저장하고, 저장된 ID를 반환합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "신청서 저장 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 입력값 (제목 누락, 비속어 등)"),
        @ApiResponse(responseCode = "401", description = "로그인 필요"),
        @ApiResponse(responseCode = "403", description = "멘티 권한 없음")
    })
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

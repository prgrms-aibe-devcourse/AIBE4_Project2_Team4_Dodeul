package org.aibe4.dodeul.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.dto.request.ConsultationEnabledRequest;
import org.aibe4.dodeul.domain.member.service.MentorProfileService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원/마이페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mentor/profile")
public class MentorProfileApiController {

    private final MentorProfileService mentorProfileService;

    @Operation(summary = "멘토 상담 가능 상태 변경", description = "멘토의 상담 가능 여부를 활성/비활성화 합니다.")
    @MemberSwaggerDocs.ToggleConsultationEnabled
    @PreAuthorize("hasRole('MENTOR')")
    @PatchMapping("/consultation-enabled")
    public CommonResponse<Void> updateConsultationEnabled(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody @Valid ConsultationEnabledRequest request
    ) {
        mentorProfileService.updateConsultationEnabled(userDetails, request.enabled());
        return CommonResponse.success(SuccessCode.UPDATE_SUCCESS, null, "상담 상태 변경 성공");
    }
}

package org.aibe4.dodeul.domain.matching.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.matching.model.dto.MatchingStatusResponse;
import org.aibe4.dodeul.domain.matching.service.MatchingService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Matching", description = "매칭 API")
@RestController
@RequestMapping("/api/matchings")
@RequiredArgsConstructor
public class MatchingApiController {

    private final MatchingService matchingService;

    @Operation(summary = "매칭 수락", description = "멘토가 매칭을 수락한다")
    @MatchingSwaggerDocs.AcceptError
    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping("/{matchingId}/acceptance")
    public CommonResponse<MatchingStatusResponse> acceptMatching(
        @PathVariable Long matchingId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        MatchingStatusResponse response = matchingService.acceptMatching(matchingId, userDetails.getMemberId());

        return CommonResponse.success(SuccessCode.SUCCESS, response, "매칭 수락을 성공했습니다.");
    }

    @Operation(summary = "매칭 거절", description = "멘토가 매칭을 거절")
    @MatchingSwaggerDocs.RejectError
    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping("/{matchingId}/rejection")
    public CommonResponse<MatchingStatusResponse> rejectMatching(
        @PathVariable Long matchingId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        MatchingStatusResponse response = matchingService.rejectMatching(matchingId, userDetails.getMemberId());

        return CommonResponse.success(SuccessCode.SUCCESS, response, "매칭 거절을 성공했습니다.");
    }

    @Operation(summary = "매칭 취소", description = "멘티가 매칭을 취소")
    @MatchingSwaggerDocs.CancelError
    @PreAuthorize("hasRole('MENTEE')")
    @PostMapping("/{matchingId}/cancellation")
    public CommonResponse<MatchingStatusResponse> cancelMatching(
        @PathVariable Long matchingId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        MatchingStatusResponse response = matchingService.cancelMatching(matchingId, userDetails.getMemberId());

        return CommonResponse.success(SuccessCode.SUCCESS, response, "매칭 취소를 성공했습니다.");
    }

    @Operation(summary = "상담 종료", description = "상담 참여자(멘토 또는 멘티)가 상담을 종료")
    @MatchingSwaggerDocs.FinishError
    @PreAuthorize("hasAnyRole('MENTOR', 'MENTEE')")
    @PostMapping("/{matchingId}/finishing")
    public CommonResponse<MatchingStatusResponse> finishMatching(
        @PathVariable Long matchingId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        MatchingStatusResponse response = matchingService.finishConsulting(matchingId, userDetails.getMemberId());

        return CommonResponse.success(SuccessCode.SUCCESS, response, "상담 종료를 성공했습니다.");
    }

    @Operation(summary = "매칭 최종 완료", description = "멘티가 매칭을 최종 완료")
    @MatchingSwaggerDocs.CompleteError
    @PreAuthorize("hasRole('MENTEE')")
    @PostMapping("/{matchingId}/completion")
    public CommonResponse<MatchingStatusResponse> completeMatching(
        @PathVariable Long matchingId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        MatchingStatusResponse response = matchingService.completeMatching(matchingId, userDetails.getMemberId());

        return CommonResponse.success(SuccessCode.SUCCESS, response, "매칭 최종 완료를 성공했습니다.");

    }
}

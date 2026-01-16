package org.aibe4.dodeul.domain.matching.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.matching.model.dto.MatchingCreateRequest;
import org.aibe4.dodeul.domain.matching.model.dto.MatchingHistoryResponse;
import org.aibe4.dodeul.domain.matching.model.dto.MatchingStatusResponse;
import org.aibe4.dodeul.domain.matching.service.MatchingService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Matching", description = "매칭 API")
@RestController
@RequestMapping("/api/matchings")
@RequiredArgsConstructor
public class MatchingApiController {

    private final MatchingService matchingService;

    @Operation(summary = "내 매칭 내역 조회", description = "사용자의 모든 매칭 내역을 조회")
    @MatchingSwaggerDocs.GetHistory
    @GetMapping
    public CommonResponse<List<MatchingHistoryResponse>> getMyMatchingHistory(
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<MatchingHistoryResponse> responses = matchingService.getMyMatchingHistory(userDetails.getMemberId());

        return CommonResponse.success(SuccessCode.SELECT_SUCCESS, responses, "내 매칭 내역 조회를 성공했습니다.");
    }

    @Operation(summary = "매칭 가능 여부 확인", description = "멘토와 멘티가 매칭이 가능한지 검증한다")
    @MatchingSwaggerDocs.CheckAvailability
    @PreAuthorize("hasRole('MENTEE')")
    @GetMapping("/availability")
    public CommonResponse<Void> checkMatchingAvailability(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Parameter(
            description = "멘토 ID (입력 시 해당 멘토와의 매칭 가능 여부 확인, 미입력 시 멘티의 신청 자격만 확인)",
            required = false,
            example = "1"
        )
        @RequestParam(required = false) Long mentorId) {

        Long menteeId = userDetails.getMemberId();
        matchingService.validateMenteeMatchingAvailability(menteeId);

        if (mentorId != null) {
            matchingService.validateMentorMatchingAvailability(mentorId);
        }

        return CommonResponse.success(SuccessCode.CHECK_SUCCESS, null, "매칭이 가능합니다.");
    }

    @Operation(summary = "매칭 신청", description = "멘티가 매칭을 신청한다")
    @MatchingSwaggerDocs.CreateMatching
    @PreAuthorize("hasRole('MENTEE')")
    @PostMapping
    public CommonResponse<MatchingStatusResponse> createMatching(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody MatchingCreateRequest request) {

        MatchingStatusResponse response = matchingService.createMatching(userDetails.getMemberId(), request);
        return CommonResponse.success(SuccessCode.CREATE_SUCCESS, response, "매칭 신청을 성공했습니다.");
    }

    @Operation(summary = "매칭 수락", description = "멘토가 매칭을 수락한다")
    @MatchingSwaggerDocs.AcceptError
    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping("/{matchingId}/acceptance")
    public CommonResponse<MatchingStatusResponse> acceptMatching(
        @Parameter(description = "매칭 ID", example = "1", required = true)
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
        @Parameter(description = "매칭 ID", example = "1", required = true)
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
        @Parameter(description = "매칭 ID", example = "1", required = true)
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
        @Parameter(description = "매칭 ID", example = "1", required = true)
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
        @Parameter(description = "매칭 ID", example = "1", required = true)
        @PathVariable Long matchingId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        MatchingStatusResponse response = matchingService.completeMatching(matchingId, userDetails.getMemberId());

        return CommonResponse.success(SuccessCode.SUCCESS, response, "매칭 최종 완료를 성공했습니다.");

    }
}

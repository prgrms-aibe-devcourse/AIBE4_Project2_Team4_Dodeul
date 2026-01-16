package org.aibe4.dodeul.domain.member.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
@Schema(description = "마이페이지 대시보드 조회 응답")
public class MyPageDashboardResponse {

    @Schema(example = "3")
    private final Long memberId;

    @Schema(description = "역할(문자열)", example = "MENTOR")
    private final String role;

    @Schema(example = "1214")
    private final String nickname;

    private final DashboardSummaryResponse summary;

    private final List<DashboardSessionItemResponse> upcomingSessions;

    private final List<DashboardSessionItemResponse> completedRecentSessions;

    public static MyPageDashboardResponse of(
        Long memberId,
        String role,
        String nickname,
        DashboardSummaryResponse summary,
        List<DashboardSessionItemResponse> upcomingSessions,
        List<DashboardSessionItemResponse> completedRecentSessions
    ) {
        return new MyPageDashboardResponse(
            memberId,
            role,
            nickname,
            summary,
            upcomingSessions,
            completedRecentSessions
        );
    }
}

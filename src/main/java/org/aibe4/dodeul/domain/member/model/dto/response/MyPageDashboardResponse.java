package org.aibe4.dodeul.domain.member.model.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class MyPageDashboardResponse {

    private final Long memberId;
    private final String role;
    private final String nickname;

    private final DashboardSummaryResponse summary;
    private final List<DashboardSessionItemResponse> upcomingSessions;

    public static MyPageDashboardResponse of(
        Long memberId,
        String role,
        String nickname,
        DashboardSummaryResponse summary,
        List<DashboardSessionItemResponse> upcomingSessions
    ) {
        return new MyPageDashboardResponse(memberId, role, nickname, summary, upcomingSessions);
    }
}

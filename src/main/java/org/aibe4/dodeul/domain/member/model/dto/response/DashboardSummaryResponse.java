package org.aibe4.dodeul.domain.member.model.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DashboardSummaryResponse {

    private final int scheduled;
    private final int ongoing;
    private final int completed;

    public static DashboardSummaryResponse of(int scheduled, int ongoing, int completed) {
        return new DashboardSummaryResponse(scheduled, ongoing, completed);
    }
}

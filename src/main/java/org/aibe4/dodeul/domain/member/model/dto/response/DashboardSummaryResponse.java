package org.aibe4.dodeul.domain.member.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "대시보드 요약(상태별 카운트)")
public class DashboardSummaryResponse {

    @Schema(description = "대기중", example = "1")
    private final int scheduled;

    @Schema(description = "진행중", example = "0")
    private final int ongoing;

    @Schema(description = "완료", example = "3")
    private final int completed;

    public static DashboardSummaryResponse of(int scheduled, int ongoing, int completed) {
        return new DashboardSummaryResponse(scheduled, ongoing, completed);
    }
}

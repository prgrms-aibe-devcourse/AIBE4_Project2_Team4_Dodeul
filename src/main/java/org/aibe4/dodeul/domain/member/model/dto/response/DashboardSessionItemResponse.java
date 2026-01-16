package org.aibe4.dodeul.domain.member.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Schema(description = "대시보드 상담 항목")
public class DashboardSessionItemResponse {

    @Schema(description = "매칭 ID", example = "1")
    private final Long matchingId;

    @Schema(description = "상담 제목", example = "이력서 피드백 요청")
    private final String title;

    @Schema(description = "상담 시작 시간", example = "2026-01-16T14:30:00")
    private final LocalDateTime startAt;

    @Schema(description = "상대 닉네임", example = "mentorKim")
    private final String counterpartName;

    public static DashboardSessionItemResponse of(
        Long matchingId,
        String title,
        LocalDateTime startAt,
        String counterpartName
    ) {
        return new DashboardSessionItemResponse(matchingId, title, startAt, counterpartName);
    }
}

package org.aibe4.dodeul.domain.member.model.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class DashboardSessionItemResponse {

    private final Long matchingId;          // TODO: 실제 식별자에 맞춰 변경
    private final String title;             // TODO: 티켓 제목/상담 주제 등
    private final LocalDateTime startAt;    // TODO: 일정 시작 시간
    private final String counterpartName;   // TODO: 상대 닉네임(멘토/멘티)

    public static DashboardSessionItemResponse of(
        Long matchingId,
        String title,
        LocalDateTime startAt,
        String counterpartName
    ) {
        return new DashboardSessionItemResponse(matchingId, title, startAt, counterpartName);
    }
}

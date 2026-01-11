package org.aibe4.dodeul.domain.matching.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MatchingStatus {
    WAITING("대기중"),

    MATCHED("승인됨"),

    REJECTED("거절됨"),

    CANCELED("취소함"),

    TIMEOUT("시간초과"),

    INREVIEW("리뷰대기"),

    COMPLETED("완료");

    private final String description;
}

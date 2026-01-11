package org.aibe4.dodeul.domain.consulting.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConsultingTag {
    CAREER("커리어/직무"),
    RESUME("이력서"),
    PORTFOLIO("포트폴리오"),
    INTERVIEW("면접"),
    CODEREVIEW("코드리뷰"),
    OTHER("기타");

    private final String description;
}

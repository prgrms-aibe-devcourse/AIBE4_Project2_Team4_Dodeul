package org.aibe4.dodeul.domain.consulting.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "상담 주제 태그 (카테고리)") // ✅ Enum 전체 설명
public enum ConsultingTag {

    @Schema(description = "커리어/직무 고민")
    CAREER("커리어/직무"),

    @Schema(description = "이력서 첨삭")
    RESUME("이력서"),

    @Schema(description = "포트폴리오 리뷰")
    PORTFOLIO("포트폴리오"),

    @Schema(description = "면접 대비")
    INTERVIEW("면접"),

    @Schema(description = "코드 리뷰 및 기술 질문")
    CODEREVIEW("코드리뷰"),

    @Schema(description = "기타 상담")
    OTHER("기타");

    private final String description;
}

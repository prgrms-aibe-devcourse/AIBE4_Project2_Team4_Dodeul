package org.aibe4.dodeul.domain.consulting.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "상담 신청서 작성 및 수정 요청 DTO")
public class ConsultingApplicationRequest {

    @Schema(hidden = true) // 멘티 ID는 시스템 내부에서 처리하므로 스웨거에서 숨김
    private Long menteeId;

    @Schema(description = "상담 제목", example = "자바 백엔드 진로 관련해서 조언을 구하고 싶습니다.")
    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 100, message = "제목은 100자를 넘을 수 없습니다.")
    private String title;

    @Schema(description = "상담 내용 상세", example = "현재 비전공자로 부트캠프 수강 중인데, 포트폴리오 방향성을 잡기가 어렵습니다.")
    @NotBlank(message = "상담 내용을 입력해주세요.")
    private String content;

    @Schema(description = "상담 카테고리 (CAREER, TECH, PORTFOLIO 등)", example = "CAREER")
    @NotNull(message = "상담 주제(카테고리)를 선택해주세요.")
    private ConsultingTag consultingTag;

    @Schema(description = "관심 기술 태그 (쉼표로 구분)", example = "Java, Spring Boot, MySQL")
    @NotBlank(message = "기술 태그를 입력해주세요.")
    private String techTags;

    @Schema(description = "첨부 파일 (이미지 등)", type = "string", format = "binary")
    private MultipartFile file;

    @Schema(hidden = true) // 파일 URL도 내부 처리용이므로 숨김
    private String fileUrl;
}

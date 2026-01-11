package org.aibe4.dodeul.domain.consulting.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;

@Getter
@Setter
@NoArgsConstructor
public class ConsultingApplicationRequest {

    // 멘티 ID는 컨트롤러에서 로그인 정보로 채워주므로 검증 불필요
    private Long menteeId;

    @NotBlank(message = "제목을 입력해주세요.") // 공백, 빈 문자열 불가
    @Size(max = 100, message = "제목은 100자를 넘을 수 없습니다.") // 길이 제한
    private String title;

    @NotBlank(message = "상담 내용을 입력해주세요.") // 필수 입력
    private String content;

    @NotNull(message = "상담 주제(카테고리)를 선택해주세요.") // Enum은 NotBlank 대신 NotNull 사용
    private ConsultingTag consultingTag;

    @NotBlank(message = "기술 태그를 입력해주세요.") // 예: "Java, Spring" (필수라면 NotBlank)
    private String techTags;

    // 파일은 아직 기능 구현 전이므로 검증 제외 (선택 사항)
    private String fileUrl;
}

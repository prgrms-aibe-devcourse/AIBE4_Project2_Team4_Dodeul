package org.aibe4.dodeul.domain.consulting.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;

@Getter
@Setter // 타임리프 Form 데이터 바인딩을 위해 Setter 추가 권장
@NoArgsConstructor
public class ConsultingApplicationRequest {

    private Long menteeId;
    private String title;
    private String content;
    private ConsultingTag consultingTag; // 카테고리 (커리어 전환 등)

    // [추가됨] 와이어프레임의 '스킬 태그' (예: "Java, React")
    private String techTags;

    // [참고] 와이어프레임의 '첨부파일'
    // 실제 파일 업로드를 하려면 MultipartFile로 바꿔야 하지만,
    // 일단 기존 코드 유지(String)하고 나중에 기능 고도화 때 수정.
    private String fileUrl;
}

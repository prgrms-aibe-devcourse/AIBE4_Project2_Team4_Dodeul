package org.aibe4.dodeul.domain.consulting.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;

@Getter
@Builder
public class ConsultingApplicationDetailResponse {
    private Long id;
    private String menteeName;
    private String title;
    private String content;
    private String fileUrl;

    // 화면에 보여줄 상담 분야 (예: CAREER, RESUME 등)
    private String consultingTag;

    // 화면에 보여줄 스킬 태그 (예: Java, Spring)
    private List<String> skillTags;

    private LocalDateTime createdAt;

    public static ConsultingApplicationDetailResponse from(ConsultingApplication entity) {
        return ConsultingApplicationDetailResponse.builder()
                .id(entity.getId())
                .menteeName("멘티(ID:" + entity.getMenteeId() + ")") // 임시
                .title(entity.getTitle())
                .content(entity.getContent())
                .fileUrl(entity.getFileUrl())

                // [핵심 수정] Enum 타입을 문자열(String)로 변환 (.name() 사용)
                // 엔티티에는 ConsultingTag 타입으로 저장되어 있으므로 .name()을 붙여야 빨간줄이 사라집니다.
                .consultingTag(entity.getConsultingTag().name())

                // [핵심 수정] 중간 테이블 리스트에서 스킬 태그 이름 뽑아내기
                .skillTags(
                        entity.getApplicationSkillTags().stream()
                                .map(ast -> ast.getSkillTag().getName())
                                .collect(Collectors.toList()))

                // BaseEntity에 있는 getCreatedAt()을 호출 (자동으로 상속됨)
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

package org.aibe4.dodeul.domain.consulting.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class ConsultingApplicationDetailResponse {

    private Long id;
    private String menteeName;
    private String title;
    private String content;
    private String category;
    private List<String> skillTags;
    private String fileUrl;

    public static ConsultingApplicationDetailResponse from(ConsultingApplication entity) {
        return ConsultingApplicationDetailResponse.builder()
            .id(entity.getId())
            .menteeName("멘티 ID: " + entity.getMenteeId())
            .title(entity.getTitle())
            .content(entity.getContent())
            .fileUrl(entity.getFileUrl())
            // [추가] 엔티티에 저장된 Enum에서 한글 설명(Description)을 가져옵니다.
            .category(entity.getConsultingTag() != null ? entity.getConsultingTag().getDescription() : null)
            // 신청서에 연결된 스킬 태그 이름들을 리스트로 변환
            .skillTags(entity.getApplicationSkillTags().stream()
                .map(mapping -> mapping.getSkillTag().getName())
                .collect(Collectors.toList()))
            .build();
    }
}

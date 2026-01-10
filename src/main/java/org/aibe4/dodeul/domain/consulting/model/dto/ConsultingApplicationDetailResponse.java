package org.aibe4.dodeul.domain.consulting.model.dto;

import lombok.Builder;
import lombok.Getter;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ConsultingApplicationDetailResponse {

    private Long id;
    private String menteeName;
    private String title;
    private String content;
    private List<String> skillTags;
    private String fileUrl;

    public static ConsultingApplicationDetailResponse from(ConsultingApplication entity) {
        return ConsultingApplicationDetailResponse.builder()
            .id(entity.getId())
            .menteeName("멘티 ID: " + entity.getMenteeId()) // 멘티 이름 대신 임시로 ID 표시
            .title(entity.getTitle())
            .content(entity.getContent())
            .fileUrl(entity.getFileUrl())
            // 신청서에 연결된 스킬 태그 이름들을 리스트로 변환
            .skillTags(entity.getApplicationSkillTags().stream()
                .map(mapping -> mapping.getSkillTag().getName())
                .collect(Collectors.toList()))
            .build();
    }
}

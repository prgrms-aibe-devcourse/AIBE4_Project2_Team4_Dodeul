package org.aibe4.dodeul.domain.consulting.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@Schema(description = "상담 신청 상세 조회 응답 DTO")
public class ConsultingApplicationDetailResponse {

    @Schema(description = "상담 신청서 ID", example = "10")
    private Long id;

    @Schema(description = "멘티 이름(또는 닉네임)", example = "코딩왕초보")
    private String menteeName;

    @Schema(description = "상담 제목", example = "자바 백엔드 로드맵 질문있습니다.")
    private String title;

    @Schema(description = "상담 내용 본문", example = "비전공자인데 취업 준비를 어떻게 해야 할까요...")
    private String content;

    @Schema(description = "상담 카테고리 (한글명)", example = "진로 상담")
    private String category;

    @Schema(description = "관심 스킬 태그 목록", example = "[\"Java\", \"Spring Boot\"]")
    private List<String> skillTags;

    @Schema(description = "첨부파일 다운로드 URL", example = "https://dodeul-bucket.s3.ap-northeast-2.amazonaws.com/files/...")
    private String fileUrl;

    public static ConsultingApplicationDetailResponse from(ConsultingApplication entity) {
        return ConsultingApplicationDetailResponse.builder()
            .id(entity.getId())
            .menteeName("멘티 ID: " + entity.getMenteeId()) // 추후 닉네임으로 변경 필요 시 수정
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

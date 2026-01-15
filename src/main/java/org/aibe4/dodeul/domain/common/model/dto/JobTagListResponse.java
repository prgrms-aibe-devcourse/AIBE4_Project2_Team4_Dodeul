package org.aibe4.dodeul.domain.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "직무 태그 목록 응답")
public class JobTagListResponse {

    @Schema(description = "직무 태그 리스트")
    private List<JobTagDto> jobTags;

    @Getter
    @AllArgsConstructor
    @Schema(description = "직무 태그 정보")
    public static class JobTagDto {
        @Schema(description = "직무명", example = "백엔드 개발자")
        private String name;
    }

    public static JobTagListResponse from(List<String> jobTagNames) {
        List<JobTagDto> jobTags = jobTagNames.stream().map(JobTagDto::new).toList();
        return new JobTagListResponse(jobTags);
    }
}

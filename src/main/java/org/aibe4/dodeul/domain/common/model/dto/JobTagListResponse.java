package org.aibe4.dodeul.domain.common.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobTagListResponse {

    private List<JobTagDto> jobTags;

    @Getter
    @AllArgsConstructor
    public static class JobTagDto {
        private String name;
    }

    public static JobTagListResponse from(List<String> jobTagNames) {
        List<JobTagDto> jobTags = jobTagNames.stream().map(JobTagDto::new).toList();
        return new JobTagListResponse(jobTags);
    }
}

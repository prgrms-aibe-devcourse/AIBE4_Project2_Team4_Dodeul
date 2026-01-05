package org.aibe4.dodeul.domain.common.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.common.dto.JobTagListResponse;
import org.aibe4.dodeul.domain.common.dto.SkillTagListResponse;
import org.aibe4.dodeul.domain.common.service.CommonService;
import org.aibe4.dodeul.global.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
public class CommonApiController {

    private final CommonService commonService;

    @GetMapping("/job-tags")
    public ResponseEntity<ApiResponse<JobTagListResponse>> getJobTags() {
        JobTagListResponse response = commonService.getJobTags();
        return ResponseEntity.ok(ApiResponse.success("직무 목록 조회 성공", response));
    }

    @GetMapping("/skill-tags")
    public ResponseEntity<ApiResponse<SkillTagListResponse>> getSkillTags() {
        SkillTagListResponse response = commonService.getSkillTags();
        return ResponseEntity.ok(ApiResponse.success("기술 스택 목록 조회 성공", response));
    }
}

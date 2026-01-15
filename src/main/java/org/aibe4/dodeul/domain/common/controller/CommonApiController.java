package org.aibe4.dodeul.domain.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.common.model.dto.JobTagListResponse;
import org.aibe4.dodeul.domain.common.model.dto.SkillTagListResponse;
import org.aibe4.dodeul.domain.common.service.CommonService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Common API", description = "공통 API (직무/스킬 태그 조회)")
@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
public class CommonApiController {

    private final CommonService commonService;

    @Operation(summary = "직무 태그 목록 조회", description = "시스템에 등록된 모든 직무 태그 목록을 조회합니다.")
    @GetMapping("/job-tags")
    public CommonResponse<JobTagListResponse> getJobTags() {
        JobTagListResponse response = commonService.getJobTags();
        return CommonResponse.success(SuccessCode.SELECT_SUCCESS, response, "직무 리스트 조회 성공");
    }

    @Operation(summary = "스킬 태그 목록 조회", description = "시스템에 등록된 모든 스킬 태그 목록을 조회합니다.")
    @GetMapping("/skill-tags")
    public CommonResponse<SkillTagListResponse> getSkillTags() {
        SkillTagListResponse response = commonService.getSkillTags();
        return CommonResponse.success(SuccessCode.SELECT_SUCCESS, response, "스킬 목록 조회 성공");
    }
}

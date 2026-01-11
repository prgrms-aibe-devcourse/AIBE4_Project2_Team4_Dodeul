package org.aibe4.dodeul.domain.common.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.common.model.dto.JobTagListResponse;
import org.aibe4.dodeul.domain.common.model.dto.SkillTagListResponse;
import org.aibe4.dodeul.domain.common.service.CommonService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
public class CommonApiController {

    private final CommonService commonService;

    @GetMapping("/job-tags")
    public CommonResponse<JobTagListResponse> getJobTags() {
        JobTagListResponse response = commonService.getJobTags();
        return CommonResponse.success(SuccessCode.SELECT_SUCCESS, response, "직무 리스트 조회 성공");
    }

    @GetMapping("/skill-tags")
    public CommonResponse<SkillTagListResponse> getSkillTags() {
        SkillTagListResponse response = commonService.getSkillTags();
        return CommonResponse.success(SuccessCode.SELECT_SUCCESS, response, "스킬 목록 조회 성공");
    }
}

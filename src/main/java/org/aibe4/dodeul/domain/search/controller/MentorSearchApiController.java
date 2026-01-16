package org.aibe4.dodeul.domain.search.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.search.model.dto.MentorSearchCondition;
import org.aibe4.dodeul.domain.search.model.dto.MentorSearchResponse;
import org.aibe4.dodeul.domain.search.service.MentorSearchService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Search", description = "멘토 검색 API")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class MentorSearchApiController {

    private final MentorSearchService mentorSearchService;

    @Operation(summary = "멘토 검색", description = "조건에 맞는 멘토 목록을 페이징하여 조회")
    @MentorSearchSwaggerDocs.SearchMentors
    @GetMapping("/mentors")
    public CommonResponse<Page<MentorSearchResponse>> searchMentors(
        @ModelAttribute MentorSearchCondition condition,
        @PageableDefault(size = 10) Pageable pageable) {

        Page<MentorSearchResponse> result = mentorSearchService.searchMentors(condition, pageable);
        return CommonResponse.success(SuccessCode.SUCCESS, result);
    }
}

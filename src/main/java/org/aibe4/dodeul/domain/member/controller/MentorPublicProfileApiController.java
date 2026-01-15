package org.aibe4.dodeul.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.dto.response.MentorPublicProfileResponse;
import org.aibe4.dodeul.domain.member.service.MentorPublicProfileService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/mentor")
public class MentorPublicProfileApiController {

    private final MentorPublicProfileService mentorPublicProfileService;

    @GetMapping("/{mentorId}")
    public CommonResponse<MentorPublicProfileResponse> getMentorProfile(@PathVariable Long mentorId) {
        MentorPublicProfileResponse data = mentorPublicProfileService.getMentorPublicProfile(mentorId);
        return CommonResponse.success(SuccessCode.SELECT_SUCCESS, data, "멘토 공개 프로필 조회 성공");
    }
}

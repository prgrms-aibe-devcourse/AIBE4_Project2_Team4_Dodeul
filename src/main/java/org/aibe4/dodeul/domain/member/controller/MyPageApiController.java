package org.aibe4.dodeul.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.dto.response.MyPageDashboardResponse;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageApiController {

    @GetMapping("/dashboard")
    public CommonResponse<MyPageDashboardResponse> dashboard(
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        // NOTE: 대시보드 데이터 연동은 후속 작업에서 진행 (라우팅 PR 범위 밖)
        return CommonResponse.success(
            SuccessCode.SUCCESS,
            null,
            "대시보드 데이터는 추후 연동 예정입니다."
        );
    }
}

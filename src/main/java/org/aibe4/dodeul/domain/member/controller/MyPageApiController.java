package org.aibe4.dodeul.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.dto.response.MyPageDashboardResponse;
import org.aibe4.dodeul.domain.member.service.MyPageService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "회원/마이페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageApiController {

    private final MyPageService myPageService;

    @Operation(summary = "마이페이지 대시보드 조회", description = "로그인한 사용자의 마이페이지 대시보드를 조회합니다.")
    @MemberSwaggerDocs.Dashboard
    @GetMapping("/dashboard")
    public CommonResponse<MyPageDashboardResponse> dashboard(
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        MyPageDashboardResponse data = myPageService.getDashboard(user.getMemberId());

        return CommonResponse.success(
            SuccessCode.SUCCESS,
            data,
            "대시보드 조회 성공"
        );
    }
}

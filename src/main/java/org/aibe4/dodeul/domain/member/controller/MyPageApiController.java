package org.aibe4.dodeul.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.dto.response.DashboardSessionItemResponse;
import org.aibe4.dodeul.domain.member.model.dto.response.DashboardSummaryResponse;
import org.aibe4.dodeul.domain.member.model.dto.response.MyPageDashboardResponse;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageApiController {

    @GetMapping("/dashboard")
    public CommonResponse<MyPageDashboardResponse> dashboard(
        @AuthenticationPrincipal CustomUserDetails user
    ) {

        // TODO: 실제 서비스/리포지토리 연동 후 대체
        DashboardSummaryResponse summary = DashboardSummaryResponse.of(
            1,  // scheduled
            2,  // ongoing
            3   // completed
        );

        // TODO: 실제 조회 로직 연동 후 대체
        List<DashboardSessionItemResponse> upcomingSessions = List.of(
            DashboardSessionItemResponse.of(
                1001L,
                "멘토",
                LocalDateTime.now().plusDays(1),
                "멘토A"
            ),
            DashboardSessionItemResponse.of(
                1002L,
                "멘토",
                LocalDateTime.now().plusDays(3),
                "멘토B"
            )
        );

        MyPageDashboardResponse data = MyPageDashboardResponse.of(
            user.getMemberId(),
            user.getRole().name(),
            user.getNickname(),
            summary,
            upcomingSessions
        );

        return CommonResponse.success(
            SuccessCode.SUCCESS,
            data,
            "대시보드 조회 성공"
        );
    }
}

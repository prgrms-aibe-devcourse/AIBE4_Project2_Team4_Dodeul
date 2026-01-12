package org.aibe4.dodeul.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.dto.response.MyPageDashboardResponse;
import org.aibe4.dodeul.global.exception.BusinessException;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
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
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS, "로그인이 필요합니다.");
        }

        MyPageDashboardResponse data = MyPageDashboardResponse.of(
            user.getMemberId(),
            user.getRole().name(),
            user.getNickname()
        );

        return CommonResponse.success(SuccessCode.SUCCESS, data, "대시보드 조회 성공");
    }
}

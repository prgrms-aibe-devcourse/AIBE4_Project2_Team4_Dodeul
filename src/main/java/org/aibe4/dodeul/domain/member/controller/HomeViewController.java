package org.aibe4.dodeul.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.service.MemberService;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeViewController {

    private final MemberService memberService;

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal CustomUserDetails user) {
        memberService.getMemberOrThrow(user.getMemberId());

        // TODO: nickname nullable 처리 후 아래 분기 적용
        // Member member = memberService.getMemberOrThrow(user.getMemberId());
        // if (member.getNickname() == null || member.getNickname().isBlank()) {
        //     return "redirect:/onboarding/nickname";
        // }

        return "redirect:/mypage/dashboard";
    }
}

package org.aibe4.dodeul.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.service.MemberService;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PostLoginRedirectController {

    private final MemberService memberService;

    @GetMapping("/post-login")
    public String home(@AuthenticationPrincipal CustomUserDetails user) {
        Member member = memberService.getMemberOrThrow(user.getMemberId());

        if (memberService.hasTemporaryNickname(member)) {
            return "redirect:/onboarding/nickname";
        }

        return "redirect:/";
    }
}

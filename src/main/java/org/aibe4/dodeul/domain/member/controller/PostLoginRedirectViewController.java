package org.aibe4.dodeul.domain.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.service.MemberService;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PostLoginRedirectViewController {

    private final MemberService memberService;

    private final RequestCache requestCache = new HttpSessionRequestCache();

    @GetMapping("/post-login")
    public String postLogin(
        HttpServletRequest request,
        HttpServletResponse response,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        if (user == null) {
            return "redirect:/auth/login";
        }

        Member member = memberService.getMemberOrThrow(user.getMemberId());

        // 1) 임시 닉네임이면 온보딩 (SavedRequest보다 우선)
        if (memberService.hasTemporaryNickname(member)) {
            return "redirect:/onboarding/nickname";
        }

        // 2) 온보딩 완료면 SavedRequest가 있으면 원래 페이지로 복귀
        SavedRequest saved = requestCache.getRequest(request, response);
        if (saved != null) {
            requestCache.removeRequest(request, response);
            String targetUrl = saved.getRedirectUrl();

            if (targetUrl != null && isSafeRedirectUrl(targetUrl)) {
                return "redirect:" + targetUrl;
            }
        }

        // 3) 없으면 홈
        return "redirect:/";
    }

    private boolean isSafeRedirectUrl(String url) {
        return url.startsWith("/") && !url.startsWith("//");
    }
}

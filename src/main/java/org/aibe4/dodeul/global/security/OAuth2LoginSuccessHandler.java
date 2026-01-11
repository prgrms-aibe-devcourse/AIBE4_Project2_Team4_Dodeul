package org.aibe4.dodeul.global.security;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.service.MemberService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final MemberService memberService;

    @PostConstruct
    public void init() {
        setDefaultTargetUrl("/post-login");
    }

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {

        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2User oauth2User) {
            Object memberIdObj = oauth2User.getAttribute("memberId");

            if (memberIdObj != null) {
                Long memberId = Long.valueOf(String.valueOf(memberIdObj));
                Member member = memberService.getMemberOrThrow(memberId);

                CustomUserDetails userDetails =
                    new CustomUserDetails(
                        member.getId(),
                        member.getEmail(),
                        member.getPasswordHash(),
                        member.getRole(),
                        member.getNickname()
                    );

                var authorities =
                    Set.of(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()));
                var newAuth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(newAuth);

                super.onAuthenticationSuccess(request, response, newAuth);
                return;
            }
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}

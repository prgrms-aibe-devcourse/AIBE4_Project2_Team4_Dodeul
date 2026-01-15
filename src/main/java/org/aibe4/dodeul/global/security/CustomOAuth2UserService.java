package org.aibe4.dodeul.global.security;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.dto.AuthSessionKeys;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.aibe4.dodeul.domain.member.service.MemberService;
import org.aibe4.dodeul.global.exception.BusinessException;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberService memberService;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauthUser = delegate.loadUser(userRequest);
        Map<String, Object> attrs = oauthUser.getAttributes();

        // google / github
        String registrationId =
            userRequest.getClientRegistration().getRegistrationId();

        String providerId;
        String email;

        if ("google".equalsIgnoreCase(registrationId)) {
            providerId = (String) attrs.get("sub");
            email = (String) attrs.get("email");

            if (providerId == null || providerId.isBlank()) {
                throw new BusinessException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "구글 사용자 식별자(sub)를 가져올 수 없습니다."
                );
            }
            if (email == null || email.isBlank()) {
                throw new BusinessException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "구글 이메일 정보를 가져올 수 없습니다."
                );
            }

        } else if ("github".equalsIgnoreCase(registrationId)) {
            Object idObj = attrs.get("id");
            providerId = (idObj == null) ? null : String.valueOf(idObj);

            // GitHub email은 null일 수 있음
            email = (String) attrs.get("email");

            if (providerId == null || providerId.isBlank()) {
                throw new BusinessException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "GitHub 사용자 식별자(id)를 가져올 수 없습니다."
                );
            }

            // email은 null이어도 진행
            if (email != null) {
                email = email.trim();
                if (email.isBlank()) {
                    email = null;
                }
            }

        } else {
            throw new BusinessException(
                ErrorCode.INVALID_INPUT_VALUE,
                "지원하지 않는 OAuth2 로그인입니다: " + registrationId
            );
        }

        Role role = getSelectedRoleFromSession();
        if (role == null) {
            throw new BusinessException(
                ErrorCode.INVALID_INPUT_VALUE,
                "역할 선택이 필요합니다. 역할을 먼저 선택해주세요."
            );
        }

        Member member;
        if ("google".equalsIgnoreCase(registrationId)) {
            member = memberService.findOrCreateGoogleMember(email, providerId, role);
        } else {
            member = memberService.findOrCreateGithubMember(email, providerId, role);
        }

        Set<SimpleGrantedAuthority> authorities =
            Set.of(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()));

        Map<String, Object> mappedAttrs = Map.of(
            "provider", registrationId,
            "providerId", providerId,
            "email", email,
            "memberId", member.getId(),
            "role", member.getRole().name()
        );

        return new DefaultOAuth2User(authorities, mappedAttrs, "providerId");
    }

    private Role getSelectedRoleFromSession() {
        ServletRequestAttributes attrs =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs == null) {
            return null;
        }

        HttpSession session = attrs.getRequest().getSession(false);
        if (session == null) {
            return null;
        }

        return (Role) session.getAttribute(AuthSessionKeys.SELECTED_ROLE);
    }
}

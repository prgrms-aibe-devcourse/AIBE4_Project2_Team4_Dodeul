package org.aibe4.dodeul.domain.member.controller;

import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class AuthTestController {

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal CustomUserDetails user) {
        return Map.of(
            "memberId", user.getMemberId(),
            "email", user.getEmail(),
            "role", user.getRole().name());
    }
}

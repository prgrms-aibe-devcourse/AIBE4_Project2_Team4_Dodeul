package org.aibe4.dodeul.domain.member.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.dto.AuthSessionKeys;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.aibe4.dodeul.domain.member.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SignupViewController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String signupForm(HttpSession session) {
        Role role = (Role) session.getAttribute(AuthSessionKeys.SELECTED_ROLE);
        if (role == null) {
            return "redirect:/onboarding/role";
        }
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(
            @RequestParam String email, @RequestParam String password, HttpSession session) {
        Role role = (Role) session.getAttribute(AuthSessionKeys.SELECTED_ROLE);
        if (role == null) return "redirect:/onboarding/role";

        memberService.registerLocal(email, password, role);

        return "redirect:/auth/login";
    }
}

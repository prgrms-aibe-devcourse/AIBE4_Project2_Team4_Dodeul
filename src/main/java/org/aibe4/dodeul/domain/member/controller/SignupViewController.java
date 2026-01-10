package org.aibe4.dodeul.domain.member.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.dto.AuthSessionKeys;
import org.aibe4.dodeul.domain.member.model.dto.SignupRequest;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.aibe4.dodeul.domain.member.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        @Valid @ModelAttribute SignupRequest request,
        BindingResult bindingResult,
        HttpSession session,
        Model model
    ) {
        Role role = (Role) session.getAttribute(AuthSessionKeys.SELECTED_ROLE);
        if (role == null) return "redirect:/onboarding/role";

        if (bindingResult.hasErrors()) {
            model.addAttribute("form", request);
            return "auth/signup";
        }

        memberService.registerLocal(request.email(), request.password(), role);
        session.removeAttribute(AuthSessionKeys.SELECTED_ROLE);
        return "redirect:/auth/login";
    }
}

package org.aibe4.dodeul.domain.member.controller;

import jakarta.servlet.http.HttpSession;
import org.aibe4.dodeul.domain.member.model.dto.AuthSessionKeys;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthEntryViewController {

    @GetMapping
    public String entry(HttpSession session, Model model, RedirectAttributes ra) {
        Role role = (Role) session.getAttribute(AuthSessionKeys.SELECTED_ROLE);
        if (role == null) {
            ra.addFlashAttribute("errorMessage", "세션이 만료되어 역할을 다시 선택해주세요.");
            return "redirect:/onboarding/role";
        }

        model.addAttribute("role", role);
        return "auth/auth-entry";
    }
}

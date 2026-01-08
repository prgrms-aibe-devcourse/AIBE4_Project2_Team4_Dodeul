package org.aibe4.dodeul.domain.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthViewController {

    @GetMapping("/auth/login")
    public String loginPage() {
        return "demo/auth/demo-login";
    }
}

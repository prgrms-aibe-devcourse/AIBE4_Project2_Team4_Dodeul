package org.aibe4.dodeul.domain.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/auth/login")
    public String loginPage() {
        return "demo/auth/demo-login";
    }
}

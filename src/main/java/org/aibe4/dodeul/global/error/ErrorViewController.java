package org.aibe4.dodeul.global.error;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorViewController {

    @GetMapping("/403")
    public String forbidden() {
        return "error/403";
    }
}

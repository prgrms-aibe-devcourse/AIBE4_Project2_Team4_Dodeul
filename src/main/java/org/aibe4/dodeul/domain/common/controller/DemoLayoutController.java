package org.aibe4.dodeul.domain.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// TODO: 공통 레이아웃 및 CSS 테스트를 위한 코드. 추후 삭제
@Controller
@RequestMapping("/demo")
public class DemoLayoutController {
    @GetMapping
    public String demo() {
        return "demo/demo";
    }
}

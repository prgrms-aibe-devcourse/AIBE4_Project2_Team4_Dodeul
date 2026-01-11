package org.aibe4.dodeul.domain.demo.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// TODO: 공통 레이아웃 및 CSS 테스트를 위한 코드. 추후 삭제
@Profile("dev")
@Controller
@RequestMapping("/demo")
public class DemoController {
    @GetMapping
    public String demo() {
        return "demo/demo";
    }

    /** 공용(base) 레이아웃 데모 */
    @GetMapping("/public")
    public String publicDemo() {
        return "demo/public-demo";
    }

    /** 멘티 레이아웃 데모 */
    @GetMapping("/mentee")
    public String menteeDemo() {
        return "demo/mentee-demo";
    }

    /** 멘토 레이아웃 데모 */
    @GetMapping("/mentor")
    public String mentorDemo() {
        return "demo/mentor-demo";
    }
}

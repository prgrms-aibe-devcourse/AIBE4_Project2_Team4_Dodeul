package org.aibe4.dodeul.domain.matching.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/matchings")
public class MatchingViewController {

    @GetMapping("/new")
    public String showRecommendPage(
        @RequestParam("applicationId") Long applicationId,
        Model model) {

        model.addAttribute("applicationId", applicationId);

        // TODO: [godqhrenf] 현재는 임시로 HTML에서 멘토 데이터를 가지고 있음
        return "matching/recommend-test";

        // TODO: [godqhrenf] 전체 멘토 조회 API 구현 후 DB에서 읽어온 데이터로 수정 예정
        // return "matching/recommend-test";
    }

    @GetMapping("/waiting")
    public String waitMatching() {
        return "matching/waiting";
    }
}

package org.aibe4.dodeul.domain.matching.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aibe4.dodeul.domain.matching.model.dto.MatchingCreateRequest;
import org.aibe4.dodeul.domain.matching.service.MatchingService;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/matchings")
@RequiredArgsConstructor
@Slf4j
public class MatchingViewController {

    private final MatchingService matchingService;

    @GetMapping("/new")
    public String showRecommendPage(
        @RequestParam("applicationId") Long applicationId,
        Model model) {

        model.addAttribute("applicationId", applicationId);

        // TODO: [godqhrenf] 현재는 임시로 HTML에서 멘토 데이터를 가지고 있음
        // TODO: [godqhrenf] 전체 멘토 조회 API 구현 후 DB에서 읽어온 데이터로 수정 예정
        return "matching/recommend";
    }

    @PostMapping()
    public String createMatching(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @ModelAttribute MatchingCreateRequest request) {

        matchingService.createMatching(userDetails.getMemberId(), request);

        return "redirect:/matchings/waiting";
    }

    @GetMapping("/waiting")
    public String waitMatching() {
        return "matching/waiting";
    }
}

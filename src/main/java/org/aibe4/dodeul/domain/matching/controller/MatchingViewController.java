package org.aibe4.dodeul.domain.matching.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.common.service.CommonService;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.aibe4.dodeul.domain.recommendation.model.dto.MentorRecommendationResponse;
import org.aibe4.dodeul.domain.recommendation.service.MentorRecommendationService;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/matchings")
public class MatchingViewController {

    private final MentorRecommendationService recommendationService;
    private final CommonService commonService;

    @GetMapping("/new")
    public String showRecommendPage(
        @RequestParam("applicationId") Long applicationId,
        Model model) {

        model.addAttribute("jobTags", commonService.getJobTags().getJobTags());
        model.addAttribute("skillTags", commonService.getSkillTags().getSkillTags());
        model.addAttribute("consultingTags", ConsultingTag.values());
        model.addAttribute("applicationId", applicationId);

        return "matching/recommend";
    }

    @GetMapping("/{applicationId}/recommendation")
    public String getRecommendFragment(
        @PathVariable Long applicationId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        Model model) {

        List<MentorRecommendationResponse> recommendedMentors =
            recommendationService.recommendMentors(userDetails.getMemberId(), applicationId);

        model.addAttribute("mentors", recommendedMentors);

        return "matching/fragments/recommend-list :: recommendList";
    }

    @GetMapping("/waiting")
    public String waitMatching(
        @RequestParam(value = "mentorName", defaultValue = "멘토") String mentorName,
        Model model) {

        model.addAttribute("mentorName", mentorName);
        return "matching/waiting";
    }
}

package org.aibe4.dodeul.domain.search.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.common.service.CommonService;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.aibe4.dodeul.domain.search.model.dto.MentorSearchCondition;
import org.aibe4.dodeul.domain.search.model.dto.MentorSearchResponse;
import org.aibe4.dodeul.domain.search.service.MentorSearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/search")
public class MentorSearchViewController {

    private final CommonService commonService;
    private final MentorSearchService mentorSearchService;

    @GetMapping("/mentors")
    public String mentorSearchPage(Model model) {
        model.addAttribute("jobTags", commonService.getJobTags().getJobTags());
        model.addAttribute("skillTags", commonService.getSkillTags().getSkillTags());
        model.addAttribute("consultingTags", ConsultingTag.values());
        return "search/mentors";
    }

    @GetMapping("/mentors/fragment")
    public String searchMentorsFragment(
        @ModelAttribute MentorSearchCondition condition,
        @PageableDefault(size = 10) Pageable pageable,
        Model model) {

        Page<MentorSearchResponse> mentors = mentorSearchService.searchMentors(condition, pageable);
        model.addAttribute("mentors", mentors);
        return "search/fragments/mentor-list :: mentorList";
    }
}

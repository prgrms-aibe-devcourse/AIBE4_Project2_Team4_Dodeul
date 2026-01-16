package org.aibe4.dodeul.domain.common.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.search.model.dto.MentorSearchResponse;
import org.aibe4.dodeul.domain.search.service.MentorSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeViewController {


    private final MentorSearchService mentorSearchService;

    @GetMapping("/")
    public String home(Model model) {

        List<MentorSearchResponse> mentors = mentorSearchService.findPopularMentors();

        model.addAttribute("mentors", mentors);

        return "common/home";
    }
}

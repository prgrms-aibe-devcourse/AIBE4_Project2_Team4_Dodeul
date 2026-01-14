package org.aibe4.dodeul.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mentor/public")
public class MentorPublicProfileViewController {

    @GetMapping("/{mentorId}")
    public String mentorPublicProfile(@PathVariable Long mentorId, Model model) {
        model.addAttribute("mentorId", mentorId);
        return "mentor/public-detail";
    }
}

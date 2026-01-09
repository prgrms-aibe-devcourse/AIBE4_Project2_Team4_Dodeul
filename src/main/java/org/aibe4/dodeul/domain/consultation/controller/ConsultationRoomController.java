package org.aibe4.dodeul.domain.consultation.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consultation.model.dto.ConsultationRoomDto;
import org.aibe4.dodeul.domain.consultation.service.ConsultationService;
import org.aibe4.dodeul.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/consultations")
@RequiredArgsConstructor
public class ConsultationRoomController {

    private final ConsultationService consultationService;

    @GetMapping("/room/{roomId}")
    public String enterRoom(@PathVariable Long roomId, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        ConsultationRoomDto consultationRoomDto = consultationService.getRoomWithApplication(roomId, userDetails.getMemberId());

        model.addAttribute("consultationRoomDto", consultationRoomDto);

        return "consultation/consultation-room";
    }
}

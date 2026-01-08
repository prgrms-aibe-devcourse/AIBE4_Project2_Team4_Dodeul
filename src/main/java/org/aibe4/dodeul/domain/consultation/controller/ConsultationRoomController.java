package org.aibe4.dodeul.domain.consultation.controller;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consultation.model.dto.ConsultationRoomDto;
import org.aibe4.dodeul.domain.consultation.service.ConsultationService;
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

    @GetMapping("/room/{roomId}/{memberId}")
    public String enterRoom(@PathVariable Long roomId, @PathVariable Long memberId, Model model) {

        ConsultationRoomDto consultationRoomDto =
            consultationService.getRoomWithApplication(
                roomId, memberId); // 현재 회원이 누군지.. security 적용되면 변경 필요

        model.addAttribute("consultationRoomDto", consultationRoomDto);

        return "consultation/consultation-room";
    }
}

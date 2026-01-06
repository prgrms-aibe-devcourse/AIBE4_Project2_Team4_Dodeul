package org.aibe4.dodeul.domain.consulting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consulting.dto.ConsultingApplicationRequest;
import org.aibe4.dodeul.domain.consulting.service.ConsultingApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Consulting Application", description = "상담 신청 관련 API")
@RestController
@RequestMapping("/api/consulting-applications")
@RequiredArgsConstructor
public class ConsultingApplicationController {

    private final ConsultingApplicationService consultingApplicationService;

    @Operation(summary = "상담 신청 등록", description = "멘티가 상담 신청서를 작성하여 등록합니다.")
    @PostMapping
    public ResponseEntity<Long> registerApplication(
            @RequestBody ConsultingApplicationRequest request) {

        // 서비스에게 일을 시키고 결과(ID)를 받음
        Long savedId = consultingApplicationService.saveApplication(request);

        // "성공(200 OK)" 응답과 함께 ID를 돌려줌
        return ResponseEntity.ok(savedId);
    }
}

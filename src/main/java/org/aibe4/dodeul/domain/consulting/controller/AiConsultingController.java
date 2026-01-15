package org.aibe4.dodeul.domain.consulting.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consulting.service.AiConsultingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "Consulting", description = "상담 관련 API (AI 초안 및 신청서)")
public class AiConsultingController {

    private final AiConsultingService aiConsultingService;

    // POST 요청: http://localhost:8080/api/ai/draft
    @PostMapping("/draft")
    public Map<String, String> generateDraft(@RequestBody Map<String, String> request) {
        String keywords = request.get("keywords");

        // 서비스 호출
        String aiResult = aiConsultingService.createDraft(keywords);

        // 결과 반환 { "content": "안녕하세요 멘토님..." }
        return Map.of("content", aiResult);
    }
}
// 106번 재커밋

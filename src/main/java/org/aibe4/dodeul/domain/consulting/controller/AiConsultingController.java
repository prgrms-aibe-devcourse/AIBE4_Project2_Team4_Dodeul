package org.aibe4.dodeul.domain.consulting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
@Tag(name = "Consulting", description = "AI 상담 초안 생성 API") // ✅ 그룹 이름 설정
public class AiConsultingController {

    private final AiConsultingService aiConsultingService;

    // POST 요청: http://localhost:8080/api/ai/draft
    @Operation(
        summary = "AI 상담 초안 생성",
        description = "키워드를 입력받아 AI가 상담 신청서 내용을 자동으로 작성해줍니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{\"keywords\": \"백엔드 취업, 자바 공부법\"}") // ✅ 스웨거에 예시 JSON을 직접 보여줌
            )
        )
    )
    @PostMapping("/draft")
    public Map<String, String> generateDraft(@RequestBody Map<String, String> request) {
        String keywords = request.get("keywords");

        // 서비스 호출
        String aiResult = aiConsultingService.createDraft(keywords);

        // 결과 반환 { "content": "안녕하세요 멘토님..." }
        return Map.of("content", aiResult);
    }
}

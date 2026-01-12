package org.aibe4.dodeul.domain.consulting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiConsultingService {

    // Groq API 키 (application-dev.yml에 등록된 키를 사용합니다)
    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String url;

    @Value("${groq.api.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    public String createDraft(String keywords) {

        // 1. Groq/OpenAI 호환 메시지 구조 생성
        Map<String, Object> message = Map.of(
            "role", "user",
            "content", "너는 IT 멘토링 상담 신청서 작성을 도와주는 조수야. 키워드: " + keywords + " 를 바탕으로 정중한 신청서 초안을 300자 내외로 작성해줘."
        );

        // 2. 요청 바디 구성 (무료 티어에서 가장 성능 좋은 llama-3.3 모델 사용)
        Map<String, Object> requestBody = Map.of(
            "model", model,
            "messages", List.of(message)
        );

        // 3. 헤더 설정 (Bearer 인증 방식 사용)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            System.out.println("Groq AI 호출 시도 (Llama 3.3)...");
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            return extractContentFromResponse(response);
        } catch (Exception e) {
            System.err.println("=== Groq 호출 실패 ===");
            System.err.println("메시지: " + e.getMessage());
            return "AI 서비스 연결에 실패했습니다: " + e.getMessage();
        }
    }

    /**
     * Groq/OpenAI 표준 JSON 응답에서 텍스트만 추출하는 메서드
     */
    private String extractContentFromResponse(Map<String, Object> response) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            System.err.println("파싱 에러: " + e.getMessage());
            return "응답 데이터를 읽는 중 오류가 발생했습니다.";
        }
    }
}

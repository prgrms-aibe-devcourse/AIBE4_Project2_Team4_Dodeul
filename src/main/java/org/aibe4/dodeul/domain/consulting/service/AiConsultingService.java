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

        // 1. [시스템 메시지] AI의 페르소나와 절대 규칙 설정 (여기가 핵심)
        String systemInstruction = """
            당신은 한국의 취업 준비생(멘티)입니다. 멘토에게 보낼 정중한 상담 신청서를 작성합니다.

            [절대 규칙]
            1. '순수 한글'만 사용하십시오. (한자, 영어 절대 금지)
            2. 괄호, 특수문자, 플레이스홀더([...])를 사용하지 마십시오.
            3. 문맥상 한자어가 필요하다면 반드시 한글로 풀어서 쓰십시오.

            [올바른 변환 예시]
            - (X) : 面接 준비가 어렵습니다.
            - (O) : 면접 준비가 어렵습니다.
            - (X) : Spec을 쌓고 싶습니다.
            - (O) : 스펙을 쌓고 싶습니다.
            - (X) : 멘토님의 助言이 필요합니다.
            - (O) : 멘토님의 조언이 필요합니다.

            위 규칙을 어길 시 시스템 오류가 발생하므로 반드시 지키십시오.
            """;

        String userContent = "다음 키워드를 포함하여 자연스러운 줄글로 작성해줘: [%s]".formatted(keywords);

        // 3. 메시지 리스트 구성 (System -> User 순서)
        List<Map<String, Object>> messages = List.of(
            Map.of("role", "system", "content", systemInstruction),
            Map.of("role", "user", "content", userContent)
        );

        // 4. 요청 바디 구성
        Map<String, Object> requestBody = Map.of(
            "model", model,
            "messages", messages,
            "temperature", 0.7, // 창의성 조절 (너무 높으면 이상한 말 함, 0.7이 적당)
            "max_tokens", 1000   // 답변 길이 제한
        );

        // 5. 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // ... (기존과 동일)
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            return extractContentFromResponse(response);
        } catch (Exception e) {
            // ... (기존과 동일)
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

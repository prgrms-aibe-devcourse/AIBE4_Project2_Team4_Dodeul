package org.aibe4.dodeul.domain.consulting.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ApplicationValidatorService {

    private final Set<String> bannedWords = new HashSet<>();

    // [성능 최적화] 패턴을 미리 컴파일해서 static 상수로 만들어 둡니다. (매번 compile 하지 않음)
    // 1. 도배 방지: 한 글자가 10회 이상 반복
    private static final Pattern REPEAT_PATTERN = Pattern.compile("(.)\\1{9,}");

    // 2. XSS 방지: <script> 태그 차단 (대소문자 무시)
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("(?i)<script.*?>.*?</script>");


    @PostConstruct
    public void loadBannedWords() {
        try {
            // 경로: src/main/resources/data/slang.txt
            ClassPathResource resource = new ClassPathResource("data/slang.txt");

            if (!resource.exists()) {
                // [오타 수정] 로그 메시지를 실제 경로에 맞게 고쳤습니다.
                log.warn("금칙어 파일을 찾을 수 없습니다: data/slang.txt");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    bannedWords.add(line.trim());
                }
            }
            log.info("금칙어 리스트 로딩 완료: {}개", bannedWords.size());

        } catch (IOException e) {
            log.error("금칙어 파일을 읽는 중 오류 발생", e);
        }
    }

    public void validateContent(String content) {
        if (content == null || content.isBlank()) {
            return;
        }

        checkBannedWords(content);
        checkSpam(content);
        checkXssAttempt(content);
    }

    private void checkBannedWords(String content) {
        for (String word : bannedWords) {
            if (content.contains(word)) {
                // 제목 검사도 같이 하니까 에러 메시지를 '본문' -> '입력하신 내용'으로 살짝 범용적으로 바꿨습니다.
                throw new IllegalArgumentException("입력하신 내용에 금지된 단어가 포함되어 있습니다.");
            }
        }
    }

    private void checkSpam(String content) {
        // 미리 만들어둔 패턴(REPEAT_PATTERN)을 재사용합니다. 훨씬 빠릅니다.
        if (REPEAT_PATTERN.matcher(content).find()) {
            throw new IllegalArgumentException("동일한 문자를 10회 이상 반복할 수 없습니다.");
        }
    }

    private void checkXssAttempt(String content) {
        // 미리 만들어둔 패턴(SCRIPT_PATTERN)을 재사용합니다.
        if (SCRIPT_PATTERN.matcher(content).find()) {
            throw new IllegalArgumentException("보안상 허용되지 않는 태그(<script>)가 포함되어 있습니다.");
        }
    }
}

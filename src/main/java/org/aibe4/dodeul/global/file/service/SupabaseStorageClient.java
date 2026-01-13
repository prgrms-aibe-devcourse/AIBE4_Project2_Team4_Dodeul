// src/main/java/org/aibe4/dodeul/global/file/service/SupabaseStorageClient.java
package org.aibe4.dodeul.global.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aibe4.dodeul.global.exception.BusinessException;
import org.aibe4.dodeul.global.file.config.SupabaseStorageProperties;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class SupabaseStorageClient {

    private final RestClient supabaseRestClient;
    private final SupabaseStorageProperties props;

    public String upload(String bucket, String objectKey, byte[] bytes, String contentType) {
        if (bytes == null || bytes.length == 0) {
            throw new BusinessException(ErrorCode.INVALID_FILE, "업로드할 파일이 비어있습니다.");
        }

        String safeContentType = normalizeContentType(contentType);

        try {
            ResponseEntity<String> response =
                supabaseRestClient
                    .post()
                    .uri(
                        uriBuilder ->
                            uriBuilder
                                .path("/storage/v1/object/" + bucket + "/" + objectKey)
                                .queryParam("upsert", "true")
                                .build())
                    .contentType(MediaType.parseMediaType(safeContentType))
                    .body(bytes)
                    .retrieve()
                    .toEntity(String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("Supabase 업로드 실패. status={}, body={}", response.getStatusCode(), response.getBody());
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
            }

            return buildPublicUrl(bucket, objectKey);

        } catch (RestClientResponseException e) {
            String body = safeBody(e);
            log.warn("Supabase 업로드 실패. status={}, responseBody={}", e.getRawStatusCode(), body);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
        } catch (Exception e) {
            log.error("Supabase 업로드 중 예기치 못한 오류", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
        }
    }

    public String buildPublicUrl(String bucket, String objectKey) {
        return props.getUrl()
            + "/storage/v1/object/public/"
            + bucket
            + "/"
            + encodeObjectKey(objectKey);
    }

    private String normalizeContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        return contentType;
    }

    private String encodeObjectKey(String objectKey) {
        String[] parts = objectKey.split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                sb.append("/");
            }
            sb.append(URLEncoder.encode(parts[i], StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    private String safeBody(RestClientResponseException e) {
        try {
            return e.getResponseBodyAsString();
        } catch (Exception ignore) {
            return "";
        }
    }
}

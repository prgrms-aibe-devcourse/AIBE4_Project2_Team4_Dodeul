// src/main/java/org/aibe4/dodeul/global/file/service/FileService.java
package org.aibe4.dodeul.global.file.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.global.exception.BusinessException;
import org.aibe4.dodeul.global.file.config.SupabaseStorageProperties;
import org.aibe4.dodeul.global.file.model.dto.response.FileUploadResponse;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024; // 10MB

    private static final Set<String> IMAGE_COMMON = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> IMAGE_PROFILE = Set.of("jpg", "jpeg", "png"); // 프로필은 gif 제외
    private static final Set<String> IMAGE_CONSULTATION = Set.of("jpg", "jpeg", "png", "gif");

    private static final Set<String> DOC_COMMON = Set.of("pdf", "txt");
    private static final Set<String> OFFICE_COMMON = Set.of("ppt", "pptx");

    private static final Set<String> CODE_COMMON =
        Set.of("java", "py", "cpp", "c", "js", "ts", "jsx", "tsx", "json", "md", "yml", "yaml", "xml", "sql");

    private final SupabaseStorageClient supabaseStorageClient;
    private final SupabaseStorageProperties props;

    @Transactional
    public FileUploadResponse upload(MultipartFile file, String prefix) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_FILE, "업로드할 파일이 비어있습니다.");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BusinessException(ErrorCode.INVALID_FILE, "파일 용량은 10MB 이하여야 합니다.");
        }

        String originFileName = file.getOriginalFilename();
        if (originFileName == null || originFileName.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_FILE, "원본 파일명이 비어있습니다.");
        }

        String ext = extractExtension(originFileName);
        Set<String> allowedExt = resolveAllowedExtensions(prefix);
        if (!allowedExt.contains(ext)) {
            throw new BusinessException(
                ErrorCode.INVALID_FILE,
                "허용되지 않는 파일 확장자입니다. 허용 확장자: " + String.join(", ", allowedExt));
        }

        String bucket = props.getStorage().getBucket();
        String objectKey = buildObjectKey(prefix, originFileName);
        String contentType = file.getContentType();

        try {
            byte[] bytes = file.getBytes();
            String fileUrl = supabaseStorageClient.upload(bucket, objectKey, bytes, contentType);

            return FileUploadResponse.builder()
                .bucket(bucket)
                .objectKey(objectKey)
                .fileUrl(fileUrl)
                .originFileName(originFileName)
                .contentType(contentType)
                .fileSize(file.getSize())
                .build();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
        }
    }

    private Set<String> resolveAllowedExtensions(String prefix) {
        String p = (prefix == null) ? "" : prefix.trim().toLowerCase(Locale.ROOT);

        // prefix는 컨텍스트가 정하는 값(예: profile, consultation, board, chat 등)으로 들어온다고 가정
        if (p.contains("profile")) {
            return IMAGE_PROFILE;
        }

        if (p.contains("consulting")) {
            return union(IMAGE_CONSULTATION, DOC_COMMON, OFFICE_COMMON);
        }

        if (p.contains("consultation") || p.contains("ticket")) {
            return union(IMAGE_CONSULTATION, DOC_COMMON, OFFICE_COMMON);
        }

        if (p.contains("board") || p.contains("post")) {
            return union(IMAGE_COMMON, DOC_COMMON, CODE_COMMON);
        }

        if (p.contains("chat")) {
            return union(IMAGE_COMMON, DOC_COMMON);
        }

        // 미지정 prefix는 안전하게 이미지+문서만 허용
        return union(IMAGE_COMMON, DOC_COMMON);
    }

    private Set<String> union(Set<String> a, Set<String> b) {
        java.util.HashSet<String> set = new java.util.HashSet<>(a);
        set.addAll(b);
        return Set.copyOf(set);
    }

    private Set<String> union(Set<String> a, Set<String> b, Set<String> c) {
        java.util.HashSet<String> set = new java.util.HashSet<>(a);
        set.addAll(b);
        set.addAll(c);
        return Set.copyOf(set);
    }

    private String extractExtension(String originalFilename) {
        int idx = originalFilename.lastIndexOf('.');
        if (idx < 0 || idx == originalFilename.length() - 1) {
            throw new BusinessException(ErrorCode.INVALID_FILE, "파일 확장자가 없습니다.");
        }
        return originalFilename.substring(idx + 1).toLowerCase(Locale.ROOT);
    }

    private String buildObjectKey(String prefix, String originalFilename) {
        String safePrefix = (prefix == null) ? "" : prefix.trim();
        if (safePrefix.startsWith("/")) {
            safePrefix = safePrefix.substring(1);
        }
        if (safePrefix.endsWith("/")) {
            safePrefix = safePrefix.substring(0, safePrefix.length() - 1);
        }

        String ext = "";
        if (originalFilename != null) {
            int idx = originalFilename.lastIndexOf('.');
            if (idx >= 0 && idx < originalFilename.length() - 1) {
                ext = originalFilename.substring(idx);
            }
        }

        String datePath = LocalDate.now().toString(); // yyyy-MM-dd
        String filename = UUID.randomUUID() + ext;

        if (safePrefix.isBlank()) {
            return datePath + "/" + filename;
        }
        return safePrefix + "/" + datePath + "/" + filename;
    }
}

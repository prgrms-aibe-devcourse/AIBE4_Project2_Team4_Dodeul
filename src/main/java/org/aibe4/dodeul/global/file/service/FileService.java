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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final SupabaseStorageClient supabaseStorageClient;
    private final SupabaseStorageProperties props;

    @Transactional
    public FileUploadResponse upload(MultipartFile file, String prefix) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_FILE, "업로드할 파일이 비어있습니다.");
        }

        String bucket = props.getStorage().getBucket();
        String originFileName = file.getOriginalFilename();
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

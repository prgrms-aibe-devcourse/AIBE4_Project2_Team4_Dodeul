// src/main/java/org/aibe4/dodeul/global/file/model/dto/response/FileUploadResponse.java
package org.aibe4.dodeul.global.file.model.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FileUploadResponse {

    private final String bucket;
    private final String objectKey;
    private final String fileUrl;
    private final String originFileName;
    private final String contentType;
    private final long fileSize;

    @Builder
    public FileUploadResponse(
        String bucket,
        String objectKey,
        String fileUrl,
        String originFileName,
        String contentType,
        long fileSize) {
        this.bucket = bucket;
        this.objectKey = objectKey;
        this.fileUrl = fileUrl;
        this.originFileName = originFileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }
}

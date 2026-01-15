// src/main/java/org/aibe4/dodeul/global/file/model/dto/response/FileUploadResponse.java
package org.aibe4.dodeul.global.file.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "파일 업로드 응답")
public class FileUploadResponse {

    @Schema(description = "저장소 버킷 이름", example = "files")
    private final String bucket;

    @Schema(description = "저장된 객체 키(경로)", example = "board/uuid_image.png")
    private final String objectKey;

    @Schema(description = "파일 접근 URL", example = "https://...")
    private final String fileUrl;

    @Schema(description = "원본 파일명", example = "image.png")
    private final String originFileName;

    @Schema(description = "MIME 타입", example = "image/png")
    private final String contentType;

    @Schema(description = "파일 크기(byte)", example = "1024")
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

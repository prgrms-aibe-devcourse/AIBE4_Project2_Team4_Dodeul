// src/main/java/org/aibe4/dodeul/global/file/controller/FileApiController.java
package org.aibe4.dodeul.global.file.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.global.exception.BusinessException;
import org.aibe4.dodeul.global.file.model.dto.response.FileUploadResponse;
import org.aibe4.dodeul.global.file.service.FileService;
import org.aibe4.dodeul.global.response.CommonResponse;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.aibe4.dodeul.global.response.enums.SuccessCode;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "File", description = "파일 업로드 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileApiController {

    private final FileService fileService;

    @Operation(summary = "파일 업로드", description = "단일 파일을 업로드하고 URL을 반환합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "파일 업로드 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 파일 요청 (파일 없음, 빈 파일 등)"),
        @ApiResponse(responseCode = "401", description = "로그인 필요")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<FileUploadResponse> upload(
        @Parameter(description = "업로드할 파일 (Multipart)", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
        @RequestParam(value = "file", required = false) List<MultipartFile> files,
        @Parameter(description = "저장 경로 접두사 (예: board, profile)", example = "board")
        @RequestParam(value = "prefix", required = false) String prefix
    ) {
        MultipartFile file = firstValidFile(files);
        FileUploadResponse response = fileService.upload(file, prefix);
        return CommonResponse.success(SuccessCode.SUCCESS, response);
    }

    private MultipartFile firstValidFile(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_FILE, "업로드할 파일이 비어있습니다.");
        }
        for (MultipartFile f : files) {
            if (f != null && !f.isEmpty()) {
                return f;
            }
        }
        throw new BusinessException(ErrorCode.INVALID_FILE, "업로드할 파일이 비어있습니다.");
    }
}

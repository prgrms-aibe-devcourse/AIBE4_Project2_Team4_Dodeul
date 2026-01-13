// src/main/java/org/aibe4/dodeul/global/file/controller/FileApiController.java
package org.aibe4.dodeul.global.file.controller;

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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileApiController {

    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<FileUploadResponse> upload(
        @RequestParam(value = "file", required = false) List<MultipartFile> files,
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

package org.aibe4.dodeul.domain.consultation.model.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType {
    IMAGE("이미지"),
    DOCUMENT("문서");

    private final String description;

    public static FileType fromExtension(String extension) {
        String ext = extension.toLowerCase();

        if (Arrays.asList("jpg", "jpeg", "png", "gif", "webp").contains(ext)) {
            return IMAGE;
        }
        if (Arrays.asList("pdf", "hwp", "docx", "xlsx", "pptx").contains(ext)) {
            return DOCUMENT;
        }

        throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
    }
}

package org.aibe4.dodeul.domain.consultation.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.BaseEntity;
import org.aibe4.dodeul.domain.consultation.model.enums.FileType;

@Entity
@Table(name = "consultation_files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsultationFile extends BaseEntity {

    @Column(name = "file_url", nullable = false, length = 2048)
    private String fileUrl;

    @Column(name = "origin_file_name", nullable = false)
    private String originFileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false, length = 20)
    private FileType fileType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false, unique = true)
    private Message message;

    @Builder
    public ConsultationFile(Message message, String fileUrl, String originFileName, Long fileSize) {
        this.message = message;
        this.fileUrl = fileUrl;
        this.originFileName = originFileName;
        this.fileSize = fileSize;
        this.fileType = FileType.fromExtension(extractExtension(originFileName));
    }

    private String extractExtension(String originFileName) {
        if (originFileName == null || !originFileName.contains(".")) {
            throw new IllegalArgumentException("확장자가 없거나 잘못된 파일명입니다.");
        }
        return originFileName.substring(originFileName.lastIndexOf(".") + 1);
    }
}

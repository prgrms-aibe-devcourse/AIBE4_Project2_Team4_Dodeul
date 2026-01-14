package org.aibe4.dodeul.domain.common.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.common.model.enums.FileDomain;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "common_files",
    indexes = {
        @Index(name = "idx_common_files_domain_message", columnList = "domain,message_id")
    }
)
public class CommonFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 공통 파일이 참조하는 도메인(게시글/상담 등)
    @Enumerated(EnumType.STRING)
    @Column(name = "domain", nullable = false, length = 30)
    private FileDomain domain;

    // 도메인 엔티티 ID (게시글이면 postId)
    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Column(name = "file_url", nullable = false, length = 2048)
    private String fileUrl;

    @Column(name = "origin_file_name", nullable = false, length = 255)
    private String originFileName;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private CommonFile(
        FileDomain domain,
        Long messageId,
        String fileUrl,
        String originFileName,
        String contentType,
        Long fileSize
    ) {
        this.domain = domain;
        this.messageId = messageId;
        this.fileUrl = fileUrl;
        this.originFileName = originFileName;
        this.contentType = contentType;
        this.fileSize = fileSize != null ? fileSize : 0L;
        this.createdAt = LocalDateTime.now();
    }

    public static CommonFile ofBoardPost(
        Long postId,
        String fileUrl,
        String originFileName,
        String contentType,
        Long fileSize
    ) {
        if (postId == null) {
            throw new IllegalArgumentException("게시글 ID는 필수입니다.");
        }
        return new CommonFile(
            FileDomain.BOARD_POST,
            postId,
            requireText(fileUrl, "파일 URL은 필수입니다."),
            requireText(originFileName, "원본 파일명은 필수입니다."),
            requireText(contentType, "콘텐츠 타입은 필수입니다."),
            fileSize
        );
    }

    // [추가] 상담 신청서용 파일 생성 메서드
    public static CommonFile ofConsultingApplication(
        Long applicationId,
        String fileUrl,
        String originFileName,
        String contentType,
        Long fileSize
    ) {
        if (applicationId == null) {
            throw new IllegalArgumentException("상담 신청서 ID는 필수입니다.");
        }
        return new CommonFile(
            FileDomain.CONSULTING_APPLICATION, // 여기를 상담용 Enum으로 지정
            applicationId,
            requireText(fileUrl, "파일 URL은 필수입니다."),
            requireText(originFileName, "원본 파일명은 필수입니다."),
            requireText(contentType, "콘텐츠 타입은 필수입니다."),
            fileSize
        );
    }

    private static String requireText(String v, String msg) {
        if (v == null || v.isBlank()) {
            throw new IllegalArgumentException(msg);
        }
        return v.trim();
    }
}

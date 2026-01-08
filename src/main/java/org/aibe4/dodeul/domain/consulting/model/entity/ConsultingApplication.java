package org.aibe4.dodeul.domain.consulting.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.BaseEntity;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "consulting_applications")
public class ConsultingApplication extends BaseEntity {

    @Column(name = "mentee_id", nullable = false)
    private Long menteeId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "consulting_tag", nullable = false) // 컬럼명 변경
    private ConsultingTag consultingTag; // 타입 및 변수명 변경

    @Column(name = "file_url")
    private String fileUrl;

    @Builder
    public ConsultingApplication(
            Long menteeId,
            String title,
            String content,
            ConsultingTag consultingTag,
            String fileUrl) {
        this.menteeId = menteeId;
        this.title = title;
        this.content = content;
        this.consultingTag = consultingTag;
        this.fileUrl = fileUrl;
    }
}

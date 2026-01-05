package org.aibe4.dodeul.domain.consulting.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "consulting_applications")
public class ConsultingApplication extends BaseEntity { // BaseEntity 상속 필수!


    @Column(name = "mentee_id", nullable = false)
    private Long menteeId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "file_url", columnDefinition = "TEXT")
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConsultingType consulting;

    @Builder
    public ConsultingApplication(Long menteeId, String title, String content, String fileUrl, ConsultingType consulting) {
        this.menteeId = menteeId;
        this.title = title;
        this.content = content;
        this.fileUrl = fileUrl;
        this.consulting = consulting;
    }
}
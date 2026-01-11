package org.aibe4.dodeul.domain.consulting.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.BaseEntity;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;

import java.util.ArrayList;
import java.util.List;

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
    @Column(name = "consulting_tag", nullable = false)
    private ConsultingTag consultingTag;

    @OneToMany(mappedBy = "consultingApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationSkillTag> applicationSkillTags = new ArrayList<>();

    @Column(name = "file_url", columnDefinition = "TEXT")
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

    // 스킬 태그를 추가하는 비즈니스 메서드 (Service에서 사용)
    public void addSkillTag(ApplicationSkillTag applicationSkillTag) {
        this.applicationSkillTags.add(applicationSkillTag);
    }

    // [추가됨] 상담 신청서 내용 수정 메서드
    public void update(String title, String content, ConsultingTag consultingTag, String fileUrl) {
        this.title = title;
        this.content = content;
        this.consultingTag = consultingTag;
        this.fileUrl = fileUrl;
    }

    // [추가됨] 스킬 태그 초기화 (수정 시 기존 태그를 비우기 위함)
    public void clearSkillTags() {
        this.applicationSkillTags.clear();
    }
}

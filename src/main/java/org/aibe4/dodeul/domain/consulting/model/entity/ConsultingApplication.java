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

    // [수정 포인트] ManyToMany 삭제 -> OneToMany로 변경
    // 타겟이 SkillTag가 아니라, 중간 테이블인 ApplicationSkillTag가 됩니다.
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
        String fileUrl) { // 빌더에서 리스트는 뺐습니다 (생성 시점엔 보통 비어있으므로)
        this.menteeId = menteeId;
        this.title = title;
        this.content = content;
        this.consultingTag = consultingTag;
        this.fileUrl = fileUrl;
    }

    // 스킬 태그를 추가하는 비즈니스 메서드 (Service에서 사용)
    public void addSkillTag(ApplicationSkillTag applicationSkillTag) {
        this.applicationSkillTags.add(applicationSkillTag);
//        applicationSkillTag.setConsultingApplication(this); // 양방향 연결
    }
}

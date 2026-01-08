package org.aibe4.dodeul.domain.consulting.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "application_skill_tags") // ERD 테이블 이름
public class ApplicationSkillTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // N:1 관계 설정 (이 중간 테이블 입장에서 신청서는 하나)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false) // ERD의 FK 컬럼명
    private ConsultingApplication consultingApplication;

    // N:1 관계 설정 (이 중간 테이블 입장에서 스킬태그는 하나)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_tag_id", nullable = false) // ERD의 FK 컬럼명
    private SkillTag skillTag;

    @Builder
    public ApplicationSkillTag(ConsultingApplication consultingApplication, SkillTag skillTag) {
        this.consultingApplication = consultingApplication;
        this.skillTag = skillTag;
    }

//    // 연관관계 편의 메서드 (선택 사항이지만 추천)
//    public void setConsultingApplication(ConsultingApplication consultingApplication) {
//        this.consultingApplication = consultingApplication;
//    }
}

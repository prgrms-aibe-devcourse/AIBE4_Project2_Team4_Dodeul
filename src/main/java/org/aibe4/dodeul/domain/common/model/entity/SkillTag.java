package org.aibe4.dodeul.domain.common.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SkillTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_tag_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // ▼▼▼ [변경] 빌더를 생성자 위에 붙입니다 ▼▼▼
    @Builder
    public SkillTag(String name) {
        this.name = name;

    }
}

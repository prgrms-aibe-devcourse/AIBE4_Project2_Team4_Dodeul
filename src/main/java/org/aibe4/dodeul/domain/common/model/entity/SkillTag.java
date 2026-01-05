package org.aibe4.dodeul.domain.common.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "skill_tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SkillTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    public SkillTag(String name) {
        this.name = name;
    }
}

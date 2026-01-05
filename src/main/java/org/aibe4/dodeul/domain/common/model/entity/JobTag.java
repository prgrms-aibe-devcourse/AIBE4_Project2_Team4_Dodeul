package org.aibe4.dodeul.domain.common.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "job_tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    public JobTag(String name) {
        this.name = name;
    }
}

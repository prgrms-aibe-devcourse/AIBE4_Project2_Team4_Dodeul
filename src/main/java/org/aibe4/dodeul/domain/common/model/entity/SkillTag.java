package org.aibe4.dodeul.domain.common.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자
@AllArgsConstructor // ★ 빌더 쓰려면 이거 필수
@Builder            // ★ 이거 없어서 빨간 줄 났던 것!
public class SkillTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_tag_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // 혹시 생성자가 따로 있다면 지우거나 두셔도 됩니다.
    // 롬복(@Builder)을 쓰면 코드가 훨씬 깔끔해집니다.
}

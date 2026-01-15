package org.aibe4.dodeul.domain.member.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "member_skill_tags",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_member_skill_tags",
            columnNames = {"member_id", "skill_id"}
        )
    }
)
public class MemberSkillTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private SkillTag skillTag;

    public MemberSkillTag(Member member, SkillTag skillTag) {
        this.member = member;
        this.skillTag = skillTag;
    }
}

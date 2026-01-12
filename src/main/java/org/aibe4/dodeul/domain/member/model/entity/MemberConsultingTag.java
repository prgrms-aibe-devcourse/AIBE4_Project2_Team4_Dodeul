package org.aibe4.dodeul.domain.member.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "member_consulting_tags",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_member_consulting_tags",
            columnNames = {"member_id", "consulting_name"}
        )
    }
)
public class MemberConsultingTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "consulting_name", nullable = false)
    private ConsultingTag consultingTag;

    public MemberConsultingTag(Member member, ConsultingTag consultingTag) {
        this.member = member;
        this.consultingTag = consultingTag;
    }
}

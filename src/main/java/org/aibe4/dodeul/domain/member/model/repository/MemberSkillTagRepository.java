package org.aibe4.dodeul.domain.member.model.repository;

import org.aibe4.dodeul.domain.member.model.entity.MemberSkillTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberSkillTagRepository extends JpaRepository<MemberSkillTag, Long> {
    void deleteAllByMemberId(Long memberId);
}

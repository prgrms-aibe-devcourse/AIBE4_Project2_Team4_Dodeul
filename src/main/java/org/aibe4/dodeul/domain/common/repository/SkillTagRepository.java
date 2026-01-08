package org.aibe4.dodeul.domain.common.repository;

import org.aibe4.dodeul.domain.common.model.entity.SkillTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkillTagRepository extends JpaRepository<SkillTag, Long> {
    Optional<SkillTag> findByName(String name);
}

package org.aibe4.dodeul.domain.common.repository;

import java.util.Optional;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillTagRepository extends JpaRepository<SkillTag, Long> {
    Optional<SkillTag> findByName(String name);
}

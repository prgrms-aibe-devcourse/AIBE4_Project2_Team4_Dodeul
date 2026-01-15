package org.aibe4.dodeul.domain.common.repository;

import org.aibe4.dodeul.domain.common.model.entity.JobTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobTagRepository extends JpaRepository<JobTag, Long> {
    Optional<JobTag> findByName(String name);
}

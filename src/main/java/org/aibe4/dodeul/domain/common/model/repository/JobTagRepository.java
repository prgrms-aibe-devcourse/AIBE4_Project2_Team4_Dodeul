package org.aibe4.dodeul.domain.common.model.repository;

import org.aibe4.dodeul.domain.common.model.entity.JobTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobTagRepository extends JpaRepository<JobTag, Long> {
}

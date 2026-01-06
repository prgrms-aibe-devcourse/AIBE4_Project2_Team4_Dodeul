package org.aibe4.dodeul.domain.matching.model.repository;

import org.aibe4.dodeul.domain.matching.model.entity.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingRepository extends JpaRepository<Matching, Long> {}

package org.aibe4.dodeul.domain.consulting.repository;

import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;
import org.springframework.data.jpa.repository.JpaRepository;

// <Entity클래스, PK타입>
public interface ConsultingApplicationRepository
        extends JpaRepository<ConsultingApplication, Long> {}

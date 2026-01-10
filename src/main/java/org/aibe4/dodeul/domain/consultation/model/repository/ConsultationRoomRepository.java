package org.aibe4.dodeul.domain.consultation.model.repository;

import org.aibe4.dodeul.domain.consultation.model.entity.ConsultationRoom;
import org.aibe4.dodeul.domain.matching.model.entity.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsultationRoomRepository extends JpaRepository<ConsultationRoom, Long> {
    Optional<ConsultationRoom> findByMatching(Matching matching);
}

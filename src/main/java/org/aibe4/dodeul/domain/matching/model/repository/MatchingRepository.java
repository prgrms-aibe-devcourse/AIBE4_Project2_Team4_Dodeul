package org.aibe4.dodeul.domain.matching.model.repository;

import org.aibe4.dodeul.domain.matching.model.entity.Matching;
import org.aibe4.dodeul.domain.matching.model.enums.MatchingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchingRepository extends JpaRepository<Matching, Long> {
    @Query("SELECT COUNT(m) FROM Matching m WHERE m.mentee.id = :menteeId AND m.status IN :statuses")
    long countByMenteeIdAndStatusIn(@Param("menteeId") Long menteeId, @Param("statuses") List<MatchingStatus> statuses);

    @Query("SELECT COUNT(m) FROM Matching m WHERE m.mentor.id = :mentorId AND m.status IN :statuses")
    long countByMentorIdAndStatusIn(@Param("mentorId") Long mentorId, @Param("statuses") List<MatchingStatus> statuses);
}

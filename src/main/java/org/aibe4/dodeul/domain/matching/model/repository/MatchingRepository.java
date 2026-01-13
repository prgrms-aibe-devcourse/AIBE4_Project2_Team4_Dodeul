package org.aibe4.dodeul.domain.matching.model.repository;

import org.aibe4.dodeul.domain.matching.model.entity.Matching;
import org.aibe4.dodeul.domain.matching.model.enums.MatchingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchingRepository extends JpaRepository<Matching, Long> {
    @Query("SELECT COUNT(m) FROM Matching m WHERE m.mentee.id = :menteeId AND m.status IN :statuses")
    long countByMenteeIdAndStatusIn(@Param("menteeId") Long menteeId, @Param("statuses") List<MatchingStatus> statuses);

    @Query("SELECT COUNT(m) FROM Matching m WHERE m.mentor.id = :mentorId AND m.status IN :statuses")
    long countByMentorIdAndStatusIn(@Param("mentorId") Long mentorId, @Param("statuses") List<MatchingStatus> statuses);

    @Query("SELECT COUNT(m) > 0 FROM Matching m WHERE m.id = :matchingId AND (m.mentee.id = :memberId OR m.mentor.id = :memberId)")
    boolean isMemberParticipantOfMatching(@Param("matchingId") Long matchingId, @Param("memberId") Long memberId);
  
    @Query("SELECT m.mentor.id, COUNT(m) FROM Matching m " +
        "WHERE m.mentor.id IN :mentorIds AND m.status IN :statuses " +
        "GROUP BY m.mentor.id")
    List<Object[]> countByMentorIdAndStatusIn(
        @Param("mentorIds") List<Long> mentorIds,
        @Param("statuses") List<MatchingStatus> statuses
    );

    List<Matching> findAllByStatusAndCreatedAtBefore(MatchingStatus status, LocalDateTime cutoffTime);

    @Query("SELECT m FROM Matching m " +
        "JOIN FETCH m.application ca " +
        "JOIN FETCH m.mentor mrt " +
        "LEFT JOIN FETCH mrt.mentorProfile " +
        "JOIN FETCH m.mentee mte " +
        "LEFT JOIN FETCH mte.menteeProfile " +
        "WHERE m.mentor.id = :memberId OR m.mentee.id = :memberId " +
        "ORDER BY m.createdAt DESC")
    List<Matching> findAllByMemberId(@Param("memberId") Long memberId);
}

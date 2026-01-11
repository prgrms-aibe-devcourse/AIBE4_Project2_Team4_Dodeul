package org.aibe4.dodeul.domain.member.model.repository;

import java.util.Optional;
import org.aibe4.dodeul.domain.member.model.entity.MentorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {

    Optional<MentorProfile> findByMemberId(Long memberId);

    boolean existsByMemberId(Long memberId);
}

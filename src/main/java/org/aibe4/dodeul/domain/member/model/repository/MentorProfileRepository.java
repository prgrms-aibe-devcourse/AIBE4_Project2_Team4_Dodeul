package org.aibe4.dodeul.domain.member.model.repository;

import org.aibe4.dodeul.domain.member.model.entity.MentorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {

    Optional<MentorProfile> findByMemberId(Long memberId);

    boolean existsByMemberId(Long memberId);
}

package org.aibe4.dodeul.domain.member.model.repository;

import jakarta.persistence.LockModeType;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.enums.Provider;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MentorSearchRepository {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    // OAuth2 로그인용
    @EntityGraph(attributePaths = {
        "mentorProfile",
        "skillTags",
        "skillTags.skillTag"
    })
    Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);

    // 공개 프로필용(mentorId 조회)
    @EntityGraph(attributePaths = {
        "mentorProfile",
        "skillTags",
        "skillTags.skillTag"
    })
    Optional<Member> findMentorPublicProfileById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from members m where m.id = :id")
    Optional<Member> findByIdWithPessimisticLock(@Param("id") Long id);
}

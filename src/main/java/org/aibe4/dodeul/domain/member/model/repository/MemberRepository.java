package org.aibe4.dodeul.domain.member.model.repository;

import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.enums.Provider;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

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
}

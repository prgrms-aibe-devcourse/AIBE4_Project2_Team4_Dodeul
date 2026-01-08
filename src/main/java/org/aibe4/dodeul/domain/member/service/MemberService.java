package org.aibe4.dodeul.domain.member.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.enums.Provider;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.aibe4.dodeul.domain.member.model.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member getMemberOrThrow(Long memberId) {
        return memberRepository
                .findById(memberId)
                .orElseThrow(() -> new IllegalStateException("Logged-in member not found"));
    }

    @Transactional
    public Long registerLocal(String email, String rawPassword, Role role) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        String passwordHash = passwordEncoder.encode(rawPassword);

        String tempNickname =
                "user_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        Member member =
                Member.builder()
                        .email(email)
                        .passwordHash(passwordHash)
                        .provider(Provider.LOCAL)
                        .providerId(null)
                        .role(role)
                        .nickname(tempNickname)
                        .build();

        return memberRepository.save(member).getId();
    }
}

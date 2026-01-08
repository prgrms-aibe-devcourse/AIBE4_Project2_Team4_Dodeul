package org.aibe4.dodeul.global.security;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member =
            memberRepository
                .findByEmail(email)
                .orElseThrow(
                    () -> new UsernameNotFoundException("Member not found: " + email));

        return new CustomUserDetails(
            member.getId(), member.getEmail(), member.getPasswordHash(), member.getRole());
    }
}

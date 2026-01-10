package org.aibe4.dodeul.global.security;

import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class CustomUserDetails implements UserDetails, CredentialsContainer {

    private final Long memberId;
    private final String email;
    private String passwordHash;
    private final Role role;
    private final String nickname;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(
            Long memberId, String email, String passwordHash, Role role, String nickname) {
        this.memberId = memberId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.nickname = nickname;
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public void eraseCredentials() {
        this.passwordHash = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.email; // Spring Security 기본 username은 email로 유지
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

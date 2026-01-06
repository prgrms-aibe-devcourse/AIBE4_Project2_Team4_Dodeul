package org.aibe4.dodeul.domain.member.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
    name = "members",
    indexes = {
        @Index(name = "idx_members_email", columnList = "email"),
        @Index(name = "idx_members_nickname", columnList = "nickname")
    }
)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    // (LOCAL 로그인만 사용, 소셜은 null 가능)
    @Column(name = "password", length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 255)
    private Provider provider;

    @Column(name = "provider_user_id", length = 255)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 255)
    private Role role;

    @Column(nullable = false, length = 20, unique = true)
    private String nickname;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @PrePersist
    private void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    private Member(
        String email,
        String passwordHash,
        Provider provider,
        String providerId,
        Role role,
        String nickname
    ) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
        this.nickname = nickname;
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}

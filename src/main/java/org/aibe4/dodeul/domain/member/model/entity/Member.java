package org.aibe4.dodeul.domain.member.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.BaseEntity;
import org.aibe4.dodeul.domain.member.model.enums.Provider;
import org.aibe4.dodeul.domain.member.model.enums.Role;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "members")
public class Member extends BaseEntity {

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private MentorProfile mentorProfile;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private MenteeProfile menteeProfile;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

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

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public Profile getProfile() {
        if (this.role == Role.MENTOR) {
            return this.mentorProfile;
        }
        return this.menteeProfile;
    }
}

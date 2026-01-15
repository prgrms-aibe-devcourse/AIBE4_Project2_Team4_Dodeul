package org.aibe4.dodeul.domain.member.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "mentee_profiles")
@EntityListeners(AuditingEntityListener.class)
public class MenteeProfile implements Profile {

    @Id
    @Column(name = "mentee_id")
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mentee_id")
    private Member member;

    @Column(name = "profile_url", columnDefinition = "TEXT")
    private String profileUrl;

    @Column(name = "intro", columnDefinition = "TEXT")
    private String intro;

    @Column(name = "job", length = 50)
    private String job;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static MenteeProfile create(Member member) {
        MenteeProfile profile = new MenteeProfile();
        profile.member = member;
        return profile;
    }

    public void updateProfile(String profileUrl, String intro, String job) {
        this.profileUrl = profileUrl;
        this.intro = intro;
        this.job = job;
    }
}

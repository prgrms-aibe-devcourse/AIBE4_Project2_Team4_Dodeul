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
@Table(name = "mentor_profiles")
@EntityListeners(AuditingEntityListener.class)
public class MentorProfile implements Profile {

    @Id
    @Column(name = "mentor_id")
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mentor_id")
    private Member member;

    @Column(name = "career_years")
    private Integer careerYears;

    @Column(name = "consultation_enabled", nullable = false)
    private boolean consultationEnabled;

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

    public static MentorProfile create(Member member) {
        MentorProfile profile = new MentorProfile();
        profile.member = member;
        profile.consultationEnabled = false;
        return profile;
    }

    public void updateProfile(
            String profileUrl,
            String intro,
            String job,
            Integer careerYears,
            boolean consultationEnabled) {
        this.profileUrl = profileUrl;
        this.intro = intro;
        this.job = job;
        this.careerYears = careerYears;
        this.consultationEnabled = consultationEnabled;
    }
}

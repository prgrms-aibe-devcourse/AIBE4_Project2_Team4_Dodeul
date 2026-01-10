package org.aibe4.dodeul.domain.member.model.entity;

/**
 * 역할(MENTOR/MENTEE)에 관계없이 공통으로 조회할 수 있는 공통 프로필 필드 모음
 */
public interface Profile {
    String getIntro();

    String getProfileUrl();

    String getJob();
}

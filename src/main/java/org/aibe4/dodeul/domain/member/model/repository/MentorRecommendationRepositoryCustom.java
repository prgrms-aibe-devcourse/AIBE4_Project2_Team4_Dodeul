package org.aibe4.dodeul.domain.member.model.repository;

import org.aibe4.dodeul.domain.member.model.entity.Member;

import java.util.List;

public interface MentorRecommendationRepositoryCustom {
    List<Member> findCandidateMentors();
}

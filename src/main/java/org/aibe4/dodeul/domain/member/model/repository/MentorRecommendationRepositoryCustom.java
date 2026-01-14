package org.aibe4.dodeul.domain.member.model.repository;

import org.aibe4.dodeul.domain.member.model.dto.MentorCandidateDto;

import java.util.List;

public interface MentorRecommendationRepositoryCustom {
    List<MentorCandidateDto> findCandidateMentorsDto();
}

package org.aibe4.dodeul.domain.member.model.repository;

import org.aibe4.dodeul.domain.search.model.dto.MentorSearchCondition;
import org.aibe4.dodeul.domain.search.model.dto.MentorSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MentorSearchRepository {
    Page<MentorSearchResponse> searchMentors(MentorSearchCondition condition, Pageable pageable);

    List<MentorSearchResponse> findPopularMentors();
}

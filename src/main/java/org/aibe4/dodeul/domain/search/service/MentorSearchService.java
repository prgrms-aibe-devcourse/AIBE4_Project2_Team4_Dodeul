package org.aibe4.dodeul.domain.search.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.member.model.repository.MemberRepository;
import org.aibe4.dodeul.domain.search.model.dto.MentorSearchCondition;
import org.aibe4.dodeul.domain.search.model.dto.MentorSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorSearchService {

    private final MemberRepository memberRepository;

    public Page<MentorSearchResponse> searchMentors(MentorSearchCondition condition, Pageable pageable) {
        return memberRepository.searchMentors(condition, pageable);
    }
}

package org.aibe4.dodeul.domain.matching.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;
import org.aibe4.dodeul.domain.consulting.service.ConsultingApplicationService;
import org.aibe4.dodeul.domain.matching.model.dto.MatchingCreateRequest;
import org.aibe4.dodeul.domain.matching.model.entity.Matching;
import org.aibe4.dodeul.domain.matching.model.repository.MatchingRepository;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.aibe4.dodeul.domain.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingService {

    private final MatchingRepository matchingRepository;

    private final MemberService memberService;
    private final ConsultingApplicationService applicationService;

    @Transactional
    public Long createMatching(Long loginMenteeId, MatchingCreateRequest request) {

        ConsultingApplication application = applicationService.findApplicationEntity(request.getApplicationId());

        if (!application.getMenteeId().equals(loginMenteeId)) {
            throw new IllegalArgumentException("본인의 신청서로만 매칭을 신청할 수 있습니다. 신청자 ID: " + loginMenteeId);
        }

        Member mentee = memberService.getMemberOrThrow(loginMenteeId);
        Member mentor = memberService.getMemberOrThrow(request.getMentorId());

        if (mentee.getRole() != Role.MENTEE || mentor.getRole() != Role.MENTOR) {
            throw new IllegalArgumentException("멘토와 멘티의 역할이 올바르지 않습니다.");
        }

        // TODO: [godqhrenf] 멘토나 멘티가 상담을 신청할 수 있는 상태인지 검사

        Matching matching = Matching.builder()
            .mentee(mentee)
            .mentor(mentor)
            .application(application)
            .build();

        return matchingRepository.save(matching).getId();
    }
}

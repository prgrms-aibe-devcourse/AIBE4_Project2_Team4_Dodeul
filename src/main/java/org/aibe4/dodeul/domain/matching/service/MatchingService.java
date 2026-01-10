package org.aibe4.dodeul.domain.matching.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;
import org.aibe4.dodeul.domain.consulting.service.ConsultingApplicationService;
import org.aibe4.dodeul.domain.matching.model.dto.MatchingCreateRequest;
import org.aibe4.dodeul.domain.matching.model.dto.MatchingStatusResponse;
import org.aibe4.dodeul.domain.matching.model.entity.Matching;
import org.aibe4.dodeul.domain.matching.model.repository.MatchingRepository;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.aibe4.dodeul.domain.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

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

    public Matching findMatchingEntity(Long matchingId) {
        return matchingRepository.findById(matchingId)
            .orElseThrow(() -> new NoSuchElementException("해당 매칭 정보를 찾을 수 없습니다."));
    }

    @Transactional
    public MatchingStatusResponse acceptMatching(Long matchingId, Long memberId) {
        Matching matching = findMatchingEntity(matchingId);

        if (!matching.getMentor().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 매칭만 수락할 수 있습니다.");
        }
        matching.accept();

        return new MatchingStatusResponse(matchingId, matching.getStatus());
    }

    @Transactional
    public MatchingStatusResponse rejectMatching(Long matchingId, Long memberId) {
        Matching matching = findMatchingEntity(matchingId);

        if (!matching.getMentor().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 매칭만 거절할 수 있습니다.");
        }
        matching.reject();

        return new MatchingStatusResponse(matchingId, matching.getStatus());
    }

    @Transactional
    public MatchingStatusResponse cancelMatching(Long matchingId, Long memberId) {
        Matching matching = findMatchingEntity(matchingId);

        if (!matching.getMentee().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인이 신청한 매칭만 취소할 수 있습니다.");
        }
        matching.cancel();

        return new MatchingStatusResponse(matchingId, matching.getStatus());
    }

    @Transactional
    public MatchingStatusResponse finishConsulting(Long matchingId, Long memberId) {
        Matching matching = findMatchingEntity(matchingId);

        if (!matching.getMentor().getId().equals(memberId) && !matching.getMentee().getId().equals(memberId)) {
            throw new IllegalArgumentException("상담 참여자만 종료할 수 있습니다.");
        }
        matching.finishConsulting();

        return new MatchingStatusResponse(matchingId, matching.getStatus());
    }

    @Transactional
    public MatchingStatusResponse completeMatching(Long matchingId, Long memberId) {
        Matching matching = findMatchingEntity(matchingId);

        if (!matching.getMentee().getId().equals(memberId)) {
            throw new IllegalArgumentException("멘티만 최종 완료를 할 수 있습니다.");
        }
        matching.complete();

        return new MatchingStatusResponse(matchingId, matching.getStatus());
    }
}

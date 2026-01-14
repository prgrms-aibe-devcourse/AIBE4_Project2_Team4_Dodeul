package org.aibe4.dodeul.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.matching.model.entity.Matching;
import org.aibe4.dodeul.domain.matching.model.enums.MatchingStatus;
import org.aibe4.dodeul.domain.matching.model.repository.MatchingRepository;
import org.aibe4.dodeul.domain.member.model.dto.response.DashboardSessionItemResponse;
import org.aibe4.dodeul.domain.member.model.dto.response.DashboardSummaryResponse;
import org.aibe4.dodeul.domain.member.model.dto.response.MyPageDashboardResponse;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final MatchingRepository matchingRepository;
    private final MemberService memberService;

    public MyPageDashboardResponse getDashboard(Long memberId) {
        Member me = memberService.getMemberOrThrow(memberId);
        Role role = me.getRole();

        // 1) summary (신청/대기 = WAITING, 진행중 = MATCHED, 완료 = COMPLETED)
        int waitingCount = (int) countByRole(role, memberId, MatchingStatus.WAITING);
        int matchedCount = (int) countByRole(role, memberId, MatchingStatus.MATCHED);
        int completedCount = (int) countByRole(role, memberId, MatchingStatus.COMPLETED);

        DashboardSummaryResponse summary = DashboardSummaryResponse.of(
            waitingCount,   // scheduled 필드지만 의미는 "신청/대기(WAITING)"
            matchedCount,   // ongoing
            completedCount  // completed
        );

        // 2) matchings 전체 내역 가져오기
        List<Matching> all = matchingRepository.findAllByMemberId(memberId);

        // 3) 진행중(MATCHED) 최근 2개 (대시보드 '진행중인 상담'에 표시)
        List<DashboardSessionItemResponse> ongoingSessions = all.stream()
            .filter(m -> m.getStatus() == MatchingStatus.MATCHED)
            .sorted(Comparator.comparing(Matching::getCreatedAt).reversed())
            .limit(2)
            .map(m -> DashboardSessionItemResponse.of(
                m.getId(),
                m.getApplication().getTitle(),
                m.getCreatedAt(), // startAt이 없으니 임시(createdAt)
                counterpartName(m, memberId)
            ))
            .toList();

        // 4) 완료(COMPLETED) 최근 3개 (대시보드 '최근 상담 내역'에 표시)
        List<DashboardSessionItemResponse> completedRecentSessions = all.stream()
            .filter(m -> m.getStatus() == MatchingStatus.COMPLETED)
            .sorted(Comparator.comparing(Matching::getCreatedAt).reversed())
            .limit(3)
            .map(m -> DashboardSessionItemResponse.of(
                m.getId(),
                m.getApplication().getTitle(),
                m.getCreatedAt(),
                counterpartName(m, memberId)
            ))
            .toList();

        return MyPageDashboardResponse.of(
            memberId,
            role.name(),
            me.getNickname(),
            summary,
            ongoingSessions,
            completedRecentSessions
        );
    }

    private long countByRole(Role role, Long memberId, MatchingStatus status) {
        if (role == Role.MENTEE) {
            return matchingRepository.countByMenteeIdAndStatusIn(memberId, List.of(status));
        }
        if (role == Role.MENTOR) {
            return matchingRepository.countByMentorIdAndStatusIn(memberId, List.of(status));
        }
        return 0;
    }

    private String counterpartName(Matching matching, Long myMemberId) {
        if (matching.getMentor().getId().equals(myMemberId)) {
            return matching.getMentee().getNickname();
        }
        return matching.getMentor().getNickname();
    }
}

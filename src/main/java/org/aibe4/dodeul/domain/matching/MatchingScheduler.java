package org.aibe4.dodeul.domain.matching;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.matching.model.entity.Matching;
import org.aibe4.dodeul.domain.matching.model.enums.MatchingStatus;
import org.aibe4.dodeul.domain.matching.model.repository.MatchingRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MatchingScheduler {

    private final MatchingRepository matchingRepository;

    /**
     * 상태가 WAITING 이고, 신청한 지 24시간이 지난 매칭은 TIMEOUT으로 종료
     */
    @Scheduled(cron = "${scheduler.matching.timeout-cron}")
    @Transactional
    public void checkAndTimeoutMatchings() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);

        List<Matching> expiredMatchings = matchingRepository.findAllByStatusAndCreatedAtBefore(
            MatchingStatus.WAITING,
            cutoffTime
        );

        if (expiredMatchings.isEmpty()) {
            return;
        }

        for (Matching matching : expiredMatchings) {
            matching.expire();
        }
    }
}

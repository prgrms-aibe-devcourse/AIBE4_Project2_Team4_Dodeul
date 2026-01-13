package org.aibe4.dodeul.domain.matching.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.aibe4.dodeul.domain.matching.model.entity.Matching;
import org.aibe4.dodeul.domain.matching.model.enums.MatchingStatus;
import org.aibe4.dodeul.domain.member.model.entity.Member;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "매칭 내역 응답 DTO")
public class MatchingHistoryResponse {

    @Schema(description = "매칭 ID", example = "1")
    private Long matchingId;

    @Schema(description = "매칭 상태", example = "WAITING")
    private MatchingStatus status;

    @Schema(description = "상담 신청서 제목", example = "백엔드 개발자 취업 관련 조언 구합니다.")
    private String applicationTitle;

    @Schema(description = "상대방 닉네임", example = "김멘토")
    private String counterpartNickname;

    @Schema(description = "상대방 프로필 이미지 URL", example = "https://dodeul-bucket/default.png")
    private String counterpartProfileUrl;

    @Schema(description = "매칭 생성일 (신청일)", example = "2026-01-12 15:48:51.290239")
    private LocalDateTime createdAt;

    public static MatchingHistoryResponse of(Matching matching, Long myMemberId) {
        Member counterpart;
        if (matching.getMentor().getId().equals(myMemberId)) {
            counterpart = matching.getMentee();
        } else {
            counterpart = matching.getMentor();
        }

        return MatchingHistoryResponse.builder()
            .matchingId(matching.getId())
            .status(matching.getStatus())
            .applicationTitle(matching.getApplication().getTitle())
            .counterpartNickname(counterpart.getNickname())
            .counterpartProfileUrl(counterpart.getProfile() != null ? counterpart.getProfile().getProfileUrl() : null)
            .createdAt(matching.getCreatedAt())
            .build();
    }
}

package org.aibe4.dodeul.domain.review.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewFormDataDto {
    private String mentorNickname;
    private String menteeNickname;

    public static ReviewFormDataDto of(String mentorNickname, String menteeNickname) {
        return ReviewFormDataDto.builder()
            .mentorNickname(mentorNickname)
            .menteeNickname(menteeNickname)
            .build();
    }
}

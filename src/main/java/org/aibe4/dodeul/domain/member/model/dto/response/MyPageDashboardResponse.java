package org.aibe4.dodeul.domain.member.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class MyPageDashboardResponse {
    private Long memberId;
    private String role;
    private String nickname;
}

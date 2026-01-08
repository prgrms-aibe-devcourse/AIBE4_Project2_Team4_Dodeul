package org.aibe4.dodeul.domain.consulting.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;

@Getter
@NoArgsConstructor
public class ConsultingApplicationRequest {

    // 임시로 사용할 멘티 ID (나중에 Member 객체로 바뀔 예정)
    private Long menteeId;

    private String title;

    private String content;

    // 태그 (CAREER, RESUME 등)
    private ConsultingTag consultingTag;

    // 파일 경로 (없을 수도 있음)
    private String fileUrl;
}

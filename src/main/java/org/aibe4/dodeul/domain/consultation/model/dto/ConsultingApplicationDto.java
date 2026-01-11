package org.aibe4.dodeul.domain.consultation.model.dto;

import lombok.Builder;
import lombok.Getter;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;

@Getter
@Builder
public class ConsultingApplicationDto {

    private String title;
    private String content;
    private String fileUrl;
    private String consultingTag;

    // private List<String> skillList;

    public static ConsultingApplicationDto of(ConsultingApplication application) {
        return ConsultingApplicationDto.builder()
                .title(application.getTitle())
                .content(application.getContent())
                .fileUrl(application.getFileUrl())
                .consultingTag(application.getConsultingTag().name())
                .build();
    }
}

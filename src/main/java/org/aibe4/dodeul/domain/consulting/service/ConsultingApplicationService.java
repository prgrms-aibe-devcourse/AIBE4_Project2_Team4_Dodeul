package org.aibe4.dodeul.domain.consulting.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;
import org.aibe4.dodeul.domain.common.repository.SkillTagRepository;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationRequest;
import org.aibe4.dodeul.domain.consulting.model.entity.ApplicationSkillTag;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;
import org.aibe4.dodeul.domain.consulting.model.repository.ConsultingApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultingApplicationService {

    private final ConsultingApplicationRepository consultingApplicationRepository;
    private final SkillTagRepository skillTagRepository;

    @Transactional
    public Long saveApplication(ConsultingApplicationRequest request) {

        // 1. DB에서 실제 스킬 태그 객체들을 찾아옵니다. (기존과 동일)
        List<SkillTag> foundSkillTags = new ArrayList<>();
        if (request.getTechTags() != null && !request.getTechTags().isEmpty()) {
            foundSkillTags =
                    Arrays.stream(request.getTechTags().split(","))
                            .map(String::trim)
                            .map(
                                    tagName ->
                                            skillTagRepository
                                                    .findByName(tagName)
                                                    .orElseThrow(
                                                            () ->
                                                                    new IllegalArgumentException(
                                                                            "존재하지 않는 태그입니다: "
                                                                                    + tagName)))
                            .collect(Collectors.toList());
        }

        // 2. 신청서 엔티티 생성 (주의: 여기서는 아직 태그를 넣지 않습니다!)
        ConsultingApplication application =
                ConsultingApplication.builder()
                        .menteeId(request.getMenteeId())
                        .title(request.getTitle())
                        .content(request.getContent())
                        .consultingTag(request.getConsultingTag())
                        .fileUrl(request.getFileUrl())
                        // .skillTags(skillTags) -> 이 부분은 삭제되었습니다.
                        .build();

        // 3. [핵심 변경] 신청서와 스킬태그를 연결하는 '중간 객체'를 만들어 넣어줍니다.
        for (SkillTag skillTag : foundSkillTags) {
            // 중간 객체 생성 (나 = 신청서, 상대 = 스킬태그)
            ApplicationSkillTag mapping =
                    ApplicationSkillTag.builder()
                            .consultingApplication(application)
                            .skillTag(skillTag)
                            .build();

            // 신청서 안에 있는 편의 메서드를 통해 연결 (아까 만든 addSkillTag 메서드 사용)
            application.addSkillTag(mapping);
        }

        // 4. 신청서를 저장하면, 연결된 중간 객체들도 같이 저장됩니다. (Cascade 옵션 덕분)
        ConsultingApplication savedApplication = consultingApplicationRepository.save(application);

        return savedApplication.getId();
    }
}

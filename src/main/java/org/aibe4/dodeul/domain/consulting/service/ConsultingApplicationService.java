package org.aibe4.dodeul.domain.consulting.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;
import org.aibe4.dodeul.domain.common.model.repository.SkillTagRepository;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationDetailResponse;
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

    // [신규 기능] 상담 신청서 상세 조회
    public ConsultingApplicationDetailResponse getApplicationDetail(Long applicationId) {
        ConsultingApplication application =
                consultingApplicationRepository
                        .findById(applicationId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "해당 상담 신청서를 찾을 수 없습니다. id=" + applicationId));

        return ConsultingApplicationDetailResponse.from(application);
    }

    // [기존 기능] 상담 신청서 저장
    @Transactional
    public Long saveApplication(ConsultingApplicationRequest request) {

        // 1. DB에서 실제 스킬 태그 객체들을 찾아옵니다.
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

        // 2. 신청서 엔티티 생성
        ConsultingApplication application =
                ConsultingApplication.builder()
                        .menteeId(request.getMenteeId())
                        .title(request.getTitle())
                        .content(request.getContent())
                        .consultingTag(request.getConsultingTag())
                        .fileUrl(request.getFileUrl())
                        .build();

        // 3. 신청서와 스킬태그를 연결하는 '중간 객체' 생성 및 연결
        for (SkillTag skillTag : foundSkillTags) {
            ApplicationSkillTag mapping =
                    ApplicationSkillTag.builder()
                            .consultingApplication(application)
                            .skillTag(skillTag)
                            .build();

            application.addSkillTag(mapping);
        }

        // 4. 저장
        ConsultingApplication savedApplication = consultingApplicationRepository.save(application);

        return savedApplication.getId();
    }
}

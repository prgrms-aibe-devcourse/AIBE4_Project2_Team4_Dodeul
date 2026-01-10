package org.aibe4.dodeul.domain.consulting.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;
import org.aibe4.dodeul.domain.common.repository.SkillTagRepository;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationDetailResponse;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationRequest;
import org.aibe4.dodeul.domain.consulting.model.entity.ApplicationSkillTag;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;
import org.aibe4.dodeul.domain.consulting.model.repository.ConsultingApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultingApplicationService {

    private final ConsultingApplicationRepository consultingApplicationRepository;
    private final SkillTagRepository skillTagRepository;

    /**
     * 상담 신청서 상세 조회 - 컨트롤러나 외부 반환용
     */
    public ConsultingApplicationDetailResponse getApplicationDetail(Long applicationId) {
        ConsultingApplication application =
            consultingApplicationRepository
                .findById(applicationId)
                .orElseThrow(
                    () ->
                        new IllegalArgumentException(
                            "해당 상담 신청서를 찾을 수 없습니다. id=" + applicationId));

        // 엔티티를 DTO로 변환해서 반환
        return ConsultingApplicationDetailResponse.from(application);
    }

    /**
     * 상담 신청서 상세 조회 - 서비스나 내부 로직용
     */
    public ConsultingApplication findApplicationEntity(Long applicationId) {
        return consultingApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new NoSuchElementException("해당 신청서를 찾을 수 없습니다: " + applicationId));
    }

    // [기존 코드] 상담 신청서 저장 (변경 없음)
    @Transactional
    public Long saveApplication(ConsultingApplicationRequest request) {

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

        ConsultingApplication application =
            ConsultingApplication.builder()
                .menteeId(request.getMenteeId())
                .title(request.getTitle())
                .content(request.getContent())
                .consultingTag(request.getConsultingTag())
                .fileUrl(request.getFileUrl())
                .build();

        for (SkillTag skillTag : foundSkillTags) {
            ApplicationSkillTag mapping =
                ApplicationSkillTag.builder()
                    .consultingApplication(application)
                    .skillTag(skillTag)
                    .build();

            application.addSkillTag(mapping);
        }

        ConsultingApplication savedApplication = consultingApplicationRepository.save(application);

        return savedApplication.getId();
    }
}

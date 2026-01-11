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
        ConsultingApplication application = findApplicationEntity(applicationId);
        return ConsultingApplicationDetailResponse.from(application);
    }

    /**
     * 상담 신청서 상세 조회 - 서비스나 내부 로직용
     */
    public ConsultingApplication findApplicationEntity(Long applicationId) {
        return consultingApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new NoSuchElementException("해당 신청서를 찾을 수 없습니다: " + applicationId));
    }

    /**
     * 상담 신청서 저장
     */
    @Transactional
    public Long saveApplication(ConsultingApplicationRequest request) {

        // 1. 태그 문자열을 객체 리스트로 변환 (DB에 없어도 에러나지 않게 수정됨)
        List<SkillTag> foundSkillTags = getSkillTagsFromString(request.getTechTags());

        // 2. 신청서 엔티티 생성
        ConsultingApplication application =
            ConsultingApplication.builder()
                .menteeId(request.getMenteeId())
                .title(request.getTitle())
                .content(request.getContent())
                .consultingTag(request.getConsultingTag())
                .fileUrl(request.getFileUrl())
                .build();

        // 3. 태그 연결
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

    /**
     * 상담 신청서 수정
     */
    @Transactional
    public void updateApplication(Long applicationId, Long memberId, ConsultingApplicationRequest request) {
        ConsultingApplication application = findApplicationEntity(applicationId);

        if (!application.getMenteeId().equals(memberId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        application.update(
            request.getTitle(),
            request.getContent(),
            request.getConsultingTag(),
            request.getFileUrl()
        );

        application.clearSkillTags();

        List<SkillTag> newTags = getSkillTagsFromString(request.getTechTags());
        for (SkillTag skillTag : newTags) {
            ApplicationSkillTag mapping = ApplicationSkillTag.builder()
                .consultingApplication(application)
                .skillTag(skillTag)
                .build();
            application.addSkillTag(mapping);
        }
    }

    /**
     * 상담 신청서 삭제
     */
    @Transactional
    public void deleteApplication(Long applicationId, Long memberId) {
        ConsultingApplication application = findApplicationEntity(applicationId);

        if (!application.getMenteeId().equals(memberId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        consultingApplicationRepository.delete(application);
    }

    /**
     * [수정됨] 문자열 태그를 List<SkillTag>로 변환
     * 존재하지 않는 태그(null)는 필터링하여 에러 발생을 방지합니다.
     */
    private List<SkillTag> getSkillTagsFromString(String techTags) {
        if (techTags == null || techTags.isBlank()) {
            return new ArrayList<>();
        }

        return Arrays.stream(techTags.split(","))
            .map(String::trim)
            .filter(name -> !name.isEmpty())
            .map(tagName -> skillTagRepository.findByName(tagName).orElse(null))
            .filter(tag -> tag != null) // DB에 없는 태그는 여기서 걸러짐
            .collect(Collectors.toList());
    }
}

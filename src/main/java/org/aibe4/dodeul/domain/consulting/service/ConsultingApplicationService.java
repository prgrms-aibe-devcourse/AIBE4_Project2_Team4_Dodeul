package org.aibe4.dodeul.domain.consulting.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;
import org.aibe4.dodeul.domain.common.repository.SkillTagRepository;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationDetailResponse;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationRequest;
import org.aibe4.dodeul.domain.consulting.model.entity.ApplicationSkillTag;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;
import org.aibe4.dodeul.domain.consulting.model.repository.ConsultingApplicationRepository;
import org.aibe4.dodeul.global.file.model.dto.response.FileUploadResponse;
import org.aibe4.dodeul.global.file.service.FileService;
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

    // [1] 검사기 및 파일 서비스 주입
    private final ApplicationValidatorService validatorService;
    private final FileService fileService; // [추가] 팀원이 만든 파일 모듈

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

        // [2] 저장하기 전에 본문 검사 실행! (비속어/도배/XSS)
        validatorService.validateContent(request.getContent());
        validatorService.validateContent(request.getTitle());

        // [추가] 파일 업로드 로직
        String fileUrl = null;
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            // "consultation" prefix를 사용하여 이미지+문서 확장자 허용
            FileUploadResponse response = fileService.upload(request.getFile(), "consultation");
            fileUrl = response.getFileUrl();
        }

        // 1. 태그 문자열을 객체 리스트로 변환
        List<SkillTag> foundSkillTags = getSkillTagsFromString(request.getTechTags());

        // 2. 신청서 엔티티 생성
        ConsultingApplication application =
            ConsultingApplication.builder()
                .menteeId(request.getMenteeId())
                .title(request.getTitle())
                .content(request.getContent())
                .consultingTag(request.getConsultingTag())
                .fileUrl(fileUrl) // [수정] 업로드된 URL 사용
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
        // [3] 수정할 때도 본문 검사 실행!
        validatorService.validateContent(request.getContent());
        validatorService.validateContent(request.getTitle());

        ConsultingApplication application = findApplicationEntity(applicationId);

        if (!application.getMenteeId().equals(memberId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        // [추가] 파일 수정 로직
        // 새 파일이 올라왔으면 업로드 후 URL 교체, 없으면 기존 URL 유지
        String fileUrl = application.getFileUrl();
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            FileUploadResponse response = fileService.upload(request.getFile(), "consultation");
            fileUrl = response.getFileUrl();
        }

        application.update(
            request.getTitle(),
            request.getContent(),
            request.getConsultingTag(),
            fileUrl // [수정] 결정된 URL 전달
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

    private List<SkillTag> getSkillTagsFromString(String techTags) {
        if (techTags == null || techTags.isBlank()) {
            return new ArrayList<>();
        }

        return Arrays.stream(techTags.split(","))
            .map(String::trim)
            .filter(name -> !name.isEmpty())
            .map(tagName -> skillTagRepository.findByName(tagName).orElse(null))
            .filter(tag -> tag != null)
            .collect(Collectors.toList());
    }

    public ConsultingApplicationRequest getRegistrationForm(Long applicationId) {
        ConsultingApplication application = findApplicationEntity(applicationId);

        ConsultingApplicationRequest form = new ConsultingApplicationRequest();
        form.setTitle(application.getTitle() != null ? application.getTitle() : "");
        form.setContent(application.getContent() != null ? application.getContent() : "");
        form.setConsultingTag(application.getConsultingTag());
        form.setFileUrl(application.getFileUrl()); // 기존 파일 URL도 폼에 담아서 보냄 (수정 화면 등에서 사용 가능)

        String tags = "";
        if (application.getApplicationSkillTags() != null) {
            tags = application.getApplicationSkillTags().stream()
                .filter(m -> m.getSkillTag() != null)
                .map(m -> m.getSkillTag().getName())
                .collect(Collectors.joining(", "));
        }
        form.setTechTags(tags);

        return form;
    }
}

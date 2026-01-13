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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultingApplicationService {

    private final ConsultingApplicationRepository consultingApplicationRepository;
    private final SkillTagRepository skillTagRepository;

    // 임시 저장 경로 (내일 파일 컨텍스트가 생기면 이 설정도 그쪽으로 옮겨질 예정입니다)
    private final String uploadPath = "C:/dodeul/uploads/";

    public ConsultingApplicationDetailResponse getApplicationDetail(Long applicationId) {
        ConsultingApplication application = findApplicationEntity(applicationId);
        return ConsultingApplicationDetailResponse.from(application);
    }

    public ConsultingApplication findApplicationEntity(Long applicationId) {
        return consultingApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new NoSuchElementException("해당 신청서를 찾을 수 없습니다: " + applicationId));
    }

    /**
     * 상담 신청서 저장
     */
    @Transactional
    public Long saveApplication(ConsultingApplicationRequest request) {

        // [내일 교체 포인트] 파일 처리 로직을 별도 메서드로 분리하여 유지보수성 향상
        String savedFileUrl = uploadFile(request.getFile());

        List<SkillTag> foundSkillTags = getSkillTagsFromString(request.getTechTags());

        ConsultingApplication application =
            ConsultingApplication.builder()
                .menteeId(request.getMenteeId())
                .title(request.getTitle())
                .content(request.getContent())
                .consultingTag(request.getConsultingTag())
                .fileUrl(savedFileUrl) // 저장된 파일명(또는 경로)이 DB에 기록됩니다.
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

    /**
     * 상담 신청서 수정
     */
    @Transactional
    public void updateApplication(Long applicationId, Long memberId, ConsultingApplicationRequest request) {
        ConsultingApplication application = findApplicationEntity(applicationId);

        if (!application.getMenteeId().equals(memberId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        // [내일 교체 포인트] 수정 시에도 새 파일이 들어오면 업로드 처리
        String savedFileUrl = application.getFileUrl();
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            savedFileUrl = uploadFile(request.getFile());
        }

        application.update(
            request.getTitle(),
            request.getContent(),
            request.getConsultingTag(),
            savedFileUrl
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
        form.setFileUrl(application.getFileUrl());

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

    /**
     * [임시 파일 업로드 메서드]
     * 내일 공통 파일 서비스가 완성되면 이 메서드 로직을 공통 서비스 호출로 대체할 예정입니다.
     */
    private String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 1. 저장 디렉토리 생성
        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();

        // 2. 파일명 중복 방지 (UUID 사용)
        String originalName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String savedName = uuid + extension;

        // 3. 물리적 저장
        try {
            file.transferTo(new File(uploadPath + savedName));
            return savedName; // DB의 fileUrl 컬럼에는 이 고유 파일명이 저장됩니다.
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }
}

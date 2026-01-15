package org.aibe4.dodeul.domain.consulting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aibe4.dodeul.domain.common.model.entity.CommonFile;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;
import org.aibe4.dodeul.domain.common.model.enums.FileDomain;
import org.aibe4.dodeul.domain.common.repository.CommonFileRepository;
import org.aibe4.dodeul.domain.common.repository.SkillTagRepository;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationDetailResponse;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationRequest;
import org.aibe4.dodeul.domain.consulting.model.entity.ApplicationSkillTag;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;
import org.aibe4.dodeul.domain.consulting.model.repository.ConsultingApplicationRepository;
import org.aibe4.dodeul.domain.matching.model.entity.Matching;
import org.aibe4.dodeul.domain.matching.model.repository.MatchingRepository;
import org.aibe4.dodeul.domain.member.model.repository.MemberRepository;
import org.aibe4.dodeul.global.exception.BusinessException;
import org.aibe4.dodeul.global.file.model.dto.response.FileUploadResponse;
import org.aibe4.dodeul.global.file.service.FileService;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultingApplicationService {

    private final MemberRepository memberRepository;
    private final ConsultingApplicationRepository consultingApplicationRepository;
    private final SkillTagRepository skillTagRepository;
    private final ApplicationValidatorService validatorService;
    private final FileService fileService;
    private final CommonFileRepository commonFileRepository; // 이 한 줄만 추가
    private final MatchingRepository matchingRepository;

    // [수정] 닉네임 조회 로직 추가 버전
    public ConsultingApplicationDetailResponse getApplicationDetail(Long applicationId) {
        // 1. 신청서 찾기
        ConsultingApplication application = findApplicationEntity(applicationId);

        // 2. DTO 변환
        ConsultingApplicationDetailResponse response = ConsultingApplicationDetailResponse.from(application);

        // 3. 멘티 ID(숫자)를 이용해서 진짜 닉네임 찾아오기
        // (만약 MemberRepository가 없거나 못 찾으면 '알수없음' 처리)
        if (memberRepository != null) {
            String nickname = memberRepository.findById(application.getMenteeId())
                .map(member -> member.getNickname()) // Member 엔티티에 getNickname() 메서드 필요
                .orElse("(알수없음)");

            // 4. DTO에 닉네임 세팅 (DTO에 setMenteeName 메서드 필요)
            response.setMenteeName(nickname);
        }

        return response;
    }

    public ConsultingApplication findApplicationEntity(Long applicationId) {
        return consultingApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new NoSuchElementException("해당 신청서를 찾을 수 없습니다: " + applicationId));
    }

    @Transactional
    public Long saveApplication(ConsultingApplicationRequest request) {
        validatorService.validateContent(request.getContent());
        validatorService.validateContent(request.getTitle());

        String fileUrl = null;
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            try {
                // 디버깅 로그 추가
                log.info("=== 파일 업로드 시도 ===");
                log.info("파일명: {}", request.getFile().getOriginalFilename());
                log.info("파일 크기: {}", request.getFile().getSize());
                log.info("ContentType: {}", request.getFile().getContentType());
                log.info("prefix: consulting");

                FileUploadResponse response = fileService.upload(request.getFile(), "consulting");
                fileUrl = response.getFileUrl();

                log.info("파일 업로드 성공: {}", fileUrl);
            } catch (Exception e) {
                log.error("=== 파일 업로드 실패 ===");
                log.error("에러 타입: {}", e.getClass().getName());
                log.error("에러 메시지: {}", e.getMessage());
                log.error("상세 스택:", e);
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
            }
        }

        List<SkillTag> foundSkillTags = getSkillTagsFromString(request.getTechTags());

        ConsultingApplication application = ConsultingApplication.builder()
            .menteeId(request.getMenteeId())
            .title(request.getTitle())
            .content(request.getContent())
            .consultingTag(request.getConsultingTag())
            .fileUrl(fileUrl)
            .build();

        for (SkillTag skillTag : foundSkillTags) {
            application.addSkillTag(ApplicationSkillTag.builder()
                .consultingApplication(application)
                .skillTag(skillTag)
                .build());
        }

        ConsultingApplication saved = consultingApplicationRepository.save(application);

        // CommonFile 저장
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            try {
                log.info("=== CommonFile 저장 시도 ===");
                CommonFile commonFile = CommonFile.ofConsultingApplication(
                    saved.getId(),
                    fileUrl,
                    request.getFile().getOriginalFilename(),
                    request.getFile().getContentType(),
                    request.getFile().getSize()
                );
                commonFileRepository.save(commonFile);
                log.info("CommonFile 저장 성공");
            } catch (Exception e) {
                log.error("CommonFile 저장 실패", e);
                throw new RuntimeException(e);
            }
        }

        return saved.getId();
    }

    @Transactional
    public void updateApplication(Long applicationId, Long memberId, ConsultingApplicationRequest request) {
        validatorService.validateContent(request.getContent());
        validatorService.validateContent(request.getTitle());

        ConsultingApplication application = findApplicationEntity(applicationId);

        if (!application.getMenteeId().equals(memberId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        String fileUrl = application.getFileUrl(); // 기본적으로 기존 URL 유지

        // 파일이 새로 들어왔을 때만 업로드 및 CommonFile 저장 수행
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            FileUploadResponse response = fileService.upload(request.getFile(), FileDomain.CONSULTING_APPLICATION.name());
            fileUrl = response.getFileUrl();

            CommonFile commonFile = CommonFile.ofConsultingApplication(
                applicationId,
                fileUrl,
                request.getFile().getOriginalFilename(),
                request.getFile().getContentType(),
                request.getFile().getSize()
            );
            commonFileRepository.save(commonFile);
        }

        application.update(
            request.getTitle(),
            request.getContent(),
            request.getConsultingTag(),
            fileUrl
        );

        application.clearSkillTags();
        List<SkillTag> newTags = getSkillTagsFromString(request.getTechTags());
        for (SkillTag skillTag : newTags) {
            application.addSkillTag(ApplicationSkillTag.builder()
                .consultingApplication(application)
                .skillTag(skillTag)
                .build());
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

        form.setMenteeId(application.getMenteeId());
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

    public Long findApplicationIdByMatchingForMentee(Long matchingId, Long menteeId) {
        Matching matching = matchingRepository.findById(matchingId)
            .orElseThrow(() -> new NoSuchElementException("매칭을 찾을 수 없습니다: " + matchingId));

        // 멘티 본인 매칭인지 검증
        if (matching.getMentee() == null || matching.getMentee().getId() == null
            || !matching.getMentee().getId().equals(menteeId)) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        // 매칭에 연결된 신청서 id 반환
        if (matching.getApplication() == null || matching.getApplication().getId() == null) {
            throw new IllegalStateException("매칭에 연결된 신청서가 없습니다.");
        }

        return matching.getApplication().getId();
    }

    public Long findApplicationIdByMatchingForMentor(Long matchingId, Long mentorId) {
        Matching matching = matchingRepository.findById(matchingId)
            .orElseThrow(() -> new NoSuchElementException("매칭을 찾을 수 없습니다: " + matchingId));

        if (matching.getMentor() == null || matching.getMentor().getId() == null
            || !matching.getMentor().getId().equals(mentorId)) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        if (matching.getApplication() == null || matching.getApplication().getId() == null) {
            throw new IllegalStateException("매칭에 연결된 신청서가 없습니다.");
        }

        return matching.getApplication().getId();
    }
}

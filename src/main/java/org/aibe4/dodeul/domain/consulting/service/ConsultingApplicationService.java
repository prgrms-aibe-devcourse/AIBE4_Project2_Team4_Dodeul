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

        // 1. 태그 문자열("Java, Spring")을 태그 객체 리스트로 변환 (도우미 메서드 사용)
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

        // 3. 태그 연결 (매핑 엔티티 생성)
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
     * [추가됨] 상담 신청서 수정
     */
    @Transactional
    public void updateApplication(Long applicationId, Long memberId, ConsultingApplicationRequest request) {
        ConsultingApplication application = findApplicationEntity(applicationId);

        // 1. 작성자 검증 (내 글이 맞는지?)
        if (!application.getMenteeId().equals(memberId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        // 2. 상태 검증 (TODO: 상담 상태가 WAITING이 아니면 수정 불가 로직 필요)
        /*
        if (application.getStatus() != ConsultingStatus.WAITING) {
            throw new IllegalStateException("대기 중인 상담만 수정할 수 있습니다.");
        }
        */

        // 3. 기본 내용 업데이트 (제목, 내용, 카테고리 등)
        application.update(
            request.getTitle(),
            request.getContent(),
            request.getConsultingTag(),
            request.getFileUrl()
        );

        // 4. 스킬 태그 업데이트 (싹 지우고 다시 등록)
        application.clearSkillTags(); // 기존 태그 연결 끊기

        List<SkillTag> newTags = getSkillTagsFromString(request.getTechTags()); // 새 태그 찾기
        for (SkillTag skillTag : newTags) {
            ApplicationSkillTag mapping = ApplicationSkillTag.builder()
                .consultingApplication(application)
                .skillTag(skillTag)
                .build();
            application.addSkillTag(mapping); // 새 태그 연결
        }

        // JPA의 변경 감지(Dirty Checking) 덕분에 따로 save()를 호출하지 않아도 DB가 수정됩니다.
    }

    /**
     * [추가됨] 상담 신청서 삭제
     */
    @Transactional
    public void deleteApplication(Long applicationId, Long memberId) {
        ConsultingApplication application = findApplicationEntity(applicationId);

        // 1. 작성자 검증
        if (!application.getMenteeId().equals(memberId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        // 2. 상태 검증 (TODO: 상담 상태 확인 필요)
        /*
        if (application.getStatus() != ConsultingStatus.WAITING) {
             throw new IllegalStateException("대기 중인 상담만 삭제할 수 있습니다.");
        }
        */

        // 3. 삭제
        consultingApplicationRepository.delete(application);
    }

    /**
     * [도우미 메서드] 문자열 태그("Java, Spring")를 List<SkillTag>로 변환
     * 저장과 수정 로직에서 공통으로 사용됩니다.
     */
    private List<SkillTag> getSkillTagsFromString(String techTags) {
        if (techTags == null || techTags.isEmpty()) {
            return new ArrayList<>();
        }

        return Arrays.stream(techTags.split(","))
            .map(String::trim)
            .map(tagName -> skillTagRepository.findByName(tagName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 태그입니다: " + tagName)))
            .collect(Collectors.toList());
    }
}

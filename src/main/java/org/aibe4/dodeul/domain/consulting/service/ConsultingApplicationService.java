package org.aibe4.dodeul.domain.consulting.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consulting.dto.ConsultingApplicationRequest;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;
import org.aibe4.dodeul.domain.consulting.repository.ConsultingApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 1. 클래스 레벨: 기본적으로 '읽기 전용' 모드로 설정
public class ConsultingApplicationService {

    private final ConsultingApplicationRepository consultingApplicationRepository;

    @Transactional // 2. 메서드 레벨: 여기는 저장을 해야 하므로 '쓰기 허용' (기본값)으로 덮어씀
    public Long saveApplication(ConsultingApplicationRequest request) {

        ConsultingApplication application =
                ConsultingApplication.builder()
                        .menteeId(request.getMenteeId())
                        .title(request.getTitle())
                        .content(request.getContent())
                        .consultingTag(request.getConsultingTag())
                        .fileUrl(request.getFileUrl())
                        .build();

        ConsultingApplication savedApplication = consultingApplicationRepository.save(application);

        return savedApplication.getId();
    }
}

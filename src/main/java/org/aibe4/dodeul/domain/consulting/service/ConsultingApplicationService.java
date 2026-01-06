package org.aibe4.dodeul.domain.consulting.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consulting.dto.ConsultingApplicationRequest;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;
import org.aibe4.dodeul.domain.consulting.repository.ConsultingApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConsultingApplicationService {

    private final ConsultingApplicationRepository consultingApplicationRepository;

    @Transactional
    public Long saveApplication(ConsultingApplicationRequest request) {

        // 1. DTO 데이터를 Entity(실제 DB 저장 객체)로 변환
        ConsultingApplication application =
                ConsultingApplication.builder()
                        .menteeId(request.getMenteeId()) // 아까 지켜낸 menteeId 사용!
                        .title(request.getTitle())
                        .content(request.getContent())
                        .consultingTag(request.getConsultingTag())
                        .fileUrl(request.getFileUrl())
                        .build();

        // 2. Repository에게 "저장해!" 라고 시킴
        ConsultingApplication savedApplication = consultingApplicationRepository.save(application);

        // 3. 저장된 번호(ID)를 반환
        return savedApplication.getId();
    }
}

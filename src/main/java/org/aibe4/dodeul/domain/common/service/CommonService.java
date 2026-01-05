package org.aibe4.dodeul.domain.common.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.common.dto.JobTagListResponse;
import org.aibe4.dodeul.domain.common.dto.SkillTagListResponse;
import org.aibe4.dodeul.domain.common.model.entity.JobTag;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;
import org.aibe4.dodeul.domain.common.repository.JobTagRepository;
import org.aibe4.dodeul.domain.common.repository.SkillTagRepository;
import org.aibe4.dodeul.global.util.EntityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommonService {

    private final JobTagRepository jobTagRepository;
    private final SkillTagRepository skillTagRepository;

    public JobTagListResponse getJobTags() {
        List<String> jobTagNames =
                EntityMapper.toNameList(jobTagRepository.findAll(), JobTag::getName);
        return JobTagListResponse.from(jobTagNames);
    }

    public SkillTagListResponse getSkillTags() {
        List<String> skillTagNames =
                EntityMapper.toNameList(skillTagRepository.findAll(), SkillTag::getName);
        return SkillTagListResponse.from(skillTagNames);
    }
}

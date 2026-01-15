package org.aibe4.dodeul.domain.member.model.repository;

import org.aibe4.dodeul.domain.member.model.entity.MemberConsultingTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberConsultingTagRepository extends JpaRepository<MemberConsultingTag, Long> {
    void deleteAllByMember_Id(Long memberId);
}

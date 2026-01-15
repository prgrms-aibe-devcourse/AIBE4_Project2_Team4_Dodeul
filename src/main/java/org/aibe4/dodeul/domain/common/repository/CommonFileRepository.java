// src/main/java/org/aibe4/dodeul/domain/common/repository/CommonFileRepository.java
package org.aibe4.dodeul.domain.common.repository;

import org.aibe4.dodeul.domain.common.model.entity.CommonFile;
import org.aibe4.dodeul.domain.common.model.enums.FileDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommonFileRepository extends JpaRepository<CommonFile, Long> {

    List<CommonFile> findAllByDomainAndMessageIdOrderByCreatedAtAsc(FileDomain domain, Long messageId);

    // NOTE: CommonFile에는 domainId 필드가 없고, 연결 키로 messageId를 사용함.
    // "AndMessageId" 네이밍 파싱 이슈 방지용으로 @Query로 고정.
    @Query(
        """
            select cf
            from CommonFile cf
            where cf.domain = :domain
              and cf.messageId = :messageId
            order by cf.id asc
            """)
    List<CommonFile> findAllByDomainAndMessageIdOrderByIdAsc(
        @Param("domain") FileDomain domain, @Param("messageId") Long messageId);

    Optional<CommonFile> findByIdAndDomainAndMessageId(Long id, FileDomain domain, Long messageId);

    List<CommonFile> findAllByMessageIdInAndDomain(List<Long> messageIds, FileDomain fileDomain);
}

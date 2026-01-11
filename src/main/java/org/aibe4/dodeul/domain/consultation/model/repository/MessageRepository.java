package org.aibe4.dodeul.domain.consultation.model.repository;

import java.util.List;
import org.aibe4.dodeul.domain.consultation.model.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConsultationRoomIdOrderByCreatedAtAsc(Long roomId); // 무한 스크롤 기능으로 구현
}

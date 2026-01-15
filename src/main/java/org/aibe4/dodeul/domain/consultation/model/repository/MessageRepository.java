package org.aibe4.dodeul.domain.consultation.model.repository;

import org.aibe4.dodeul.domain.consultation.model.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // 커서를 기준으로 이전 메시지들을 조회하는 쿼리
    @Query("SELECT m FROM Message m " +
        "WHERE m.consultationRoom.id = :roomId AND m.id < :lastMessageId " +
        "ORDER BY m.id DESC")
    Slice<Message> findOldMessagesByCursor(@Param("roomId") Long roomId, @Param("lastMessageId") Long lastMessageId, Pageable pageable);

    // 특정 방의 가장 최신 메시지들을 조회하는 쿼리
    @Query("SELECT m FROM Message m " +
        "WHERE m.consultationRoom.id = :roomId " +
        "ORDER BY m.id DESC")
    Slice<Message> findLatestMessages(Long roomId, Pageable pageable);

    // 특정 채팅방에서 타입이 IMAGE 또는 FILE인 메시지만 최신순 조회
    @Query("SELECT m FROM Message m " +
        "JOIN FETCH m.sender " +
        "WHERE m.consultationRoom.id = :roomId " +
        "AND m.messageType IN (org.aibe4.dodeul.domain.consultation.model.enums.MessageType.IMAGE, " +
        "               org.aibe4.dodeul.domain.consultation.model.enums.MessageType.FILE) " +
        "ORDER BY m.id DESC")
    List<Message> findFileMessagesByRoomId(@Param("roomId") Long roomId);
}

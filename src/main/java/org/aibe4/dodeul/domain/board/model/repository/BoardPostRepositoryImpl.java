package org.aibe4.dodeul.domain.board.model.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostListRequest;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class BoardPostRepositoryImpl implements BoardPostRepositoryCustom {

    @PersistenceContext private EntityManager em;

    @Override
    public Page<BoardPostListResponse> findPosts(
            BoardPostListRequest request, Long memberId, Pageable pageable) {
        // TODO: QueryDSL / JPQL 구현 예정
        // 임시: 빈 페이지 반환 (컴파일용)
        List<BoardPostListResponse> items = List.of();
        return new PageImpl<>(items, pageable, 0);
    }
}

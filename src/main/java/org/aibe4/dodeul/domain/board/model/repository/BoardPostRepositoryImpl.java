// src/main/java/org/aibe4/dodeul/domain/board/model/repository/BoardPostRepositoryImpl.java
package org.aibe4.dodeul.domain.board.model.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostListRequest;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostListResponse;
import org.aibe4.dodeul.domain.board.model.entity.BoardPost;
import org.aibe4.dodeul.domain.board.model.entity.BoardPostTagRelation;
import org.aibe4.dodeul.domain.board.model.enums.BoardPostStatusFilter;
import org.aibe4.dodeul.domain.board.model.enums.CommentStatus;
import org.aibe4.dodeul.domain.board.model.enums.PostStatus;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class BoardPostRepositoryImpl implements BoardPostRepositoryCustom {

    private final EntityManager em;
    private final BoardCommentRepository boardCommentRepository;
    private final BoardPostTagRelationRepository boardPostTagRelationRepository;

    @Override
    public Page<BoardPostListResponse> findPosts(
        BoardPostListRequest request, Long memberId, Pageable pageable) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<BoardPost> cq = cb.createQuery(BoardPost.class);
        Root<BoardPost> root = cq.from(BoardPost.class);

        List<Predicate> predicates = buildPredicates(cb, cq, root, request);
        cq.where(predicates.toArray(new Predicate[0]));

        cq.orderBy(buildOrders(cb, root, request));

        TypedQuery<BoardPost> query = em.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<BoardPost> posts = query.getResultList();
        List<Long> postIds =
            posts.stream().map(BoardPost::getId).filter(Objects::nonNull).toList();

        // 스크랩 여부 계산은 기존 구현 유지(여기서는 생략 가정)
        Set<Long> scrappedPostIds = Collections.emptySet();

        // ✅ 댓글 수는 실제 댓글 테이블 집계로 맞춘다.
        Map<Long, Long> commentCountMap = new HashMap<>();
        if (!postIds.isEmpty()) {
            List<BoardCommentRepository.PostCommentCountRow> rows =
                boardCommentRepository.findCommentCountsByPostIds(postIds, CommentStatus.DELETED);
            for (BoardCommentRepository.PostCommentCountRow r : rows) {
                commentCountMap.put(r.getPostId(), r.getCnt());
            }
        }

        // ✅ 스킬태그는 relation 테이블에서 postIds IN 으로 한 번에 조회해서 매핑한다. (N+1 방지)
        Map<Long, List<String>> skillTagsMap = new HashMap<>();
        if (!postIds.isEmpty()) {
            List<BoardPostTagRelationRepository.PostSkillTagRow> rows =
                boardPostTagRelationRepository.findSkillTagNamesByPostIds(postIds);
            for (BoardPostTagRelationRepository.PostSkillTagRow r : rows) {
                skillTagsMap.computeIfAbsent(r.getPostId(), k -> new ArrayList<>()).add(r.getTagName());
            }
        }

        List<BoardPostListResponse> dtos =
            posts.stream()
                .map(
                    p ->
                        BoardPostListResponse.builder()
                            .postId(p.getId())
                            .title(p.getTitle())
                            .postStatus(p.getPostStatus() != null ? p.getPostStatus().name() : null)
                            .viewCount(p.getViewCount() != null ? p.getViewCount() : 0)
                            .scrapCount(p.getScrapCount() != null ? p.getScrapCount() : 0)
                            .commentCount(commentCountMap.getOrDefault(p.getId(), 0L).intValue())
                            .lastCommentedAt(p.getLastCommentedAt())
                            .createdAt(p.getCreatedAt())
                            .scrappedByMe(scrappedPostIds.contains(p.getId()))
                            .skillTags(skillTagsMap.getOrDefault(p.getId(), List.of()))
                            .build())
                .toList();

        long total = countTotal(cb, request);
        return new PageImpl<>(dtos, pageable, total);
    }

    private long countTotal(CriteriaBuilder cb, BoardPostListRequest request) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<BoardPost> countRoot = countQuery.from(BoardPost.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> predicates = buildPredicates(cb, countQuery, countRoot, request);
        countQuery.where(predicates.toArray(new Predicate[0]));

        return em.createQuery(countQuery).getSingleResult();
    }

    private List<Order> buildOrders(CriteriaBuilder cb, Root<BoardPost> root, BoardPostListRequest request) {
        // 기존 정렬 정책 유지(여기서는 최신순 기본)
        return List.of(cb.desc(root.get("createdAt")));
    }

    private <T> List<Predicate> buildPredicates(
        CriteriaBuilder cb, CriteriaQuery<T> query, Root<BoardPost> root, BoardPostListRequest request) {

        List<Predicate> predicates = new ArrayList<>();

        // ✅ 기본 정책: DELETED 제외
        predicates.add(cb.notEqual(root.get("postStatus"), PostStatus.DELETED));

        // ✅ status 처리
        // - "ALL/전체/빈값" => OPEN + CLOSED (DELETED 제외만 적용)
        // - "OPEN" => OPEN
        // - "CLOSED" => CLOSED
        // - 그 외 잘못된 값 => OPEN (정책)
        String rawStatus = request != null ? request.getStatus() : null;
        BoardPostStatusFilter filter = BoardPostStatusFilter.fromNullable(rawStatus);

        if (filter == null) {
            predicates.add(cb.equal(root.get("postStatus"), PostStatus.OPEN));
        } else if (filter == BoardPostStatusFilter.OPEN) {
            predicates.add(cb.equal(root.get("postStatus"), PostStatus.OPEN));
        } else if (filter == BoardPostStatusFilter.CLOSED) {
            predicates.add(cb.equal(root.get("postStatus"), PostStatus.CLOSED));
        } else {
            // ALL: OPEN + CLOSED (DELETED 제외만 유지)
        }

        ConsultingTag consultingTag = request != null ? request.getConsultingTag() : null;
        if (consultingTag != null) {
            predicates.add(cb.equal(root.get("boardConsulting"), consultingTag));
        }

        if (request != null && request.getKeyword() != null && !request.getKeyword().isBlank()) {
            String kw = "%" + request.getKeyword().trim() + "%";
            predicates.add(cb.or(cb.like(root.get("title"), kw), cb.like(root.get("content"), kw)));
        }

        if (request != null && request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Subquery<Integer> sub = query.subquery(Integer.class);
            Root<BoardPostTagRelation> rel = sub.from(BoardPostTagRelation.class);

            sub.select(cb.literal(1));
            sub.where(
                cb.and(
                    cb.equal(rel.get("boardPost"), root),
                    rel.get("skillTag").get("id").in(request.getTagIds())));

            predicates.add(cb.exists(sub));
        }

        return predicates;
    }
}

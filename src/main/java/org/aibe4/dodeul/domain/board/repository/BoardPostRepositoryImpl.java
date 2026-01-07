// src/main/java/org/aibe4/dodeul/domain/board/repository/BoardPostRepositoryImpl.java
package org.aibe4.dodeul.domain.board.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;
import org.aibe4.dodeul.domain.board.model.dto.BoardPostListRequest;
import org.aibe4.dodeul.domain.board.model.dto.BoardPostListResponse;
import org.aibe4.dodeul.domain.board.model.entity.BoardPost;
import org.aibe4.dodeul.domain.board.model.entity.BoardPostTagRelation;
import org.aibe4.dodeul.domain.board.model.enums.PostStatus;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class BoardPostRepositoryImpl implements BoardPostRepositoryCustom {

    private final EntityManager em;

    public BoardPostRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Page<BoardPostListResponse> findPosts(
            BoardPostListRequest request, Long memberId, Pageable pageable) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<BoardPost> countRoot = countQuery.from(BoardPost.class);

        List<Predicate> countPredicates = buildPredicates(cb, countQuery, countRoot, request);
        countQuery.select(cb.countDistinct(countRoot));
        if (!countPredicates.isEmpty()) {
            countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
        }

        Long total = em.createQuery(countQuery).getSingleResult();
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        CriteriaQuery<BoardPost> dataQuery = cb.createQuery(BoardPost.class).distinct(true);
        Root<BoardPost> root = dataQuery.from(BoardPost.class);

        List<Predicate> predicates = buildPredicates(cb, dataQuery, root, request);
        if (!predicates.isEmpty()) {
            dataQuery.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();
            for (Sort.Order o : pageable.getSort()) {
                if (o.isAscending()) {
                    orders.add(cb.asc(root.get(o.getProperty())));
                } else {
                    orders.add(cb.desc(root.get(o.getProperty())));
                }
            }
            dataQuery.orderBy(orders);
        } else {
            dataQuery.orderBy(cb.desc(root.get("createdAt")));
        }

        TypedQuery<BoardPost> typedQuery = em.createQuery(dataQuery);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<BoardPost> posts = typedQuery.getResultList();
        List<Long> postIds = posts.stream().map(BoardPost::getId).collect(Collectors.toList());

        final Set<Long> scrappedPostIds;
        if (memberId != null && !postIds.isEmpty()) {
            TypedQuery<Long> q =
                    em.createQuery(
                            "select s.boardPost.id "
                                    + "from BoardPostScrap s "
                                    + "where s.memberId = :memberId "
                                    + "and s.boardPost.id in :postIds",
                            Long.class);
            q.setParameter("memberId", memberId);
            q.setParameter("postIds", postIds);
            scrappedPostIds = new HashSet<>(q.getResultList());
        } else {
            scrappedPostIds = Collections.emptySet();
        }

        Map<Long, List<String>> tagMap = new HashMap<>();
        if (!postIds.isEmpty()) {
            TypedQuery<Object[]> relQ =
                    em.createQuery(
                            "select r.boardPost.id, r.skillTagEntity.name "
                                    + "from BoardPostTagRelation r "
                                    + "where r.boardPost.id in :postIds",
                            Object[].class);
            relQ.setParameter("postIds", postIds);

            for (Object[] row : relQ.getResultList()) {
                Long postId = ((Number) row[0]).longValue();
                String tagName = (String) row[1];
                if (tagName == null) {
                    continue;
                }
                tagMap.computeIfAbsent(postId, k -> new ArrayList<>()).add(tagName);
            }
        }

        List<BoardPostListResponse> dtos =
                posts.stream()
                        .map(
                                p ->
                                        BoardPostListResponse.builder()
                                                .postId(p.getId())
                                                .consultingTag(p.getBoardConsulting())
                                                .title(p.getTitle())
                                                .postStatus(
                                                        p.getPostStatus() != null
                                                                ? p.getPostStatus().name()
                                                                : null)
                                                .viewCount(
                                                        p.getViewCount() != null
                                                                ? p.getViewCount()
                                                                : 0)
                                                .scrapCount(
                                                        p.getScrapCount() != null
                                                                ? p.getScrapCount()
                                                                : 0)
                                                .commentCount(
                                                        p.getCommentCount() != null
                                                                ? p.getCommentCount()
                                                                : 0)
                                                .lastCommentedAt(p.getLastCommentedAt())
                                                .createdAt(p.getCreatedAt())
                                                .scrappedByMe(scrappedPostIds.contains(p.getId()))
                                                .skillTags(
                                                        tagMap.getOrDefault(
                                                                p.getId(), Collections.emptyList()))
                                                .build())
                        .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, total);
    }

    private List<Predicate> buildPredicates(
            CriteriaBuilder cb,
            CriteriaQuery<?> query,
            Root<BoardPost> root,
            BoardPostListRequest request) {

        List<Predicate> predicates = new ArrayList<>();

        PostStatus status = PostStatus.OPEN;
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                status = PostStatus.valueOf(request.getStatus().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                // 잘못된 상태 값이 들어온 경우 기본 OPEN 유지
            }
        }
        predicates.add(cb.equal(root.get("postStatus"), status));

        ConsultingTag consultingTag = request.getConsultingTag();
        if (consultingTag != null) {
            predicates.add(cb.equal(root.get("boardConsulting"), consultingTag));
        }

        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            String kw = "%" + request.getKeyword().trim() + "%";
            predicates.add(cb.or(cb.like(root.get("title"), kw), cb.like(root.get("content"), kw)));
        }

        if (request.getSkillTagIds() != null && !request.getSkillTagIds().isEmpty()) {
            Subquery<Integer> sub = query.subquery(Integer.class);
            Root<BoardPostTagRelation> rel = sub.from(BoardPostTagRelation.class);

            Predicate p1 = cb.equal(rel.get("boardPost").get("id"), root.get("id"));
            Predicate p2 = rel.get("skillTagEntity").get("id").in(request.getSkillTagIds());

            sub.select(cb.literal(1));
            sub.where(cb.and(p1, p2));

            predicates.add(cb.exists(sub));
        }

        return predicates;
    }
}

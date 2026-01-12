package org.aibe4.dodeul.domain.board.model.repository;

import jakarta.persistence.*;
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

    // 트랜잭션 내에서 "실패하는 쿼리"를 던지면 rollback-only로 마킹될 수 있어서
    // 메타데이터로 컬럼명을 먼저 확정 후 1번만 조회한다.
    private volatile String scrapMemberColumn;
    private volatile String scrapPostColumn;

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
        List<Long> postIds = posts.stream().map(BoardPost::getId).filter(Objects::nonNull).toList();

        // ✅ scrappedByMe: memberId + postIds 기준으로 한 번에 조회
        Set<Long> scrappedPostIds = findScrappedPostIds(memberId, postIds);

        // ✅ 댓글 수: 실제 댓글 테이블 집계
        Map<Long, Long> commentCountMap = new HashMap<>();
        if (!postIds.isEmpty()) {
            List<BoardCommentRepository.PostCommentCountRow> rows =
                boardCommentRepository.findCommentCountsByPostIds(postIds, CommentStatus.DELETED);
            for (BoardCommentRepository.PostCommentCountRow r : rows) {
                commentCountMap.put(r.getPostId(), r.getCnt());
            }
        }

        // ✅ 스킬태그: relation 테이블에서 postIds IN 한 번에 조회 (N+1 방지)
        Map<Long, List<String>> skillTagsMap = new HashMap<>();
        if (!postIds.isEmpty()) {
            List<BoardPostTagRelationRepository.PostSkillTagRow> rows =
                boardPostTagRelationRepository.findSkillTagNamesByPostIds(postIds);
            for (BoardPostTagRelationRepository.PostSkillTagRow r : rows) {
                skillTagsMap.computeIfAbsent(r.getPostId(), k -> new ArrayList<>())
                    .add(r.getTagName());
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

    private Set<Long> findScrappedPostIds(Long memberId, List<Long> postIds) {
        if (memberId == null || postIds == null || postIds.isEmpty()) {
            return Set.of();
        }

        ensureScrapColumnsResolved();

        // 컬럼을 못 찾았으면(환경 차이) 스크랩 기능만 비활성화하고 목록은 정상 노출
        if (scrapMemberColumn == null || scrapPostColumn == null) {
            return Set.of();
        }

        try {
            Query q =
                em.createNativeQuery(
                    "select s."
                        + scrapPostColumn
                        + " from board_post_scraps s where s."
                        + scrapMemberColumn
                        + " = :memberId and s."
                        + scrapPostColumn
                        + " in (:postIds)");
            q.setParameter("memberId", memberId);
            q.setParameter("postIds", postIds);

            @SuppressWarnings("unchecked")
            List<Number> rows = q.getResultList();

            Set<Long> result = new HashSet<>();
            for (Number n : rows) {
                if (n != null) {
                    result.add(n.longValue());
                }
            }
            return result;
        } catch (IllegalStateException | TransactionRequiredException e) {
            // 영속성 컨텍스트/트랜잭션 상태 이슈가 있어도 목록 API 자체는 성공시키기
            return Set.of();
        } catch (PersistenceException e) {
            // 쿼리 실패가 rollback-only로 번지는 걸 막기 위해 스크랩만 무시
            return Set.of();
        }
    }

    private void ensureScrapColumnsResolved() {
        if (scrapMemberColumn != null && scrapPostColumn != null) {
            return;
        }

        synchronized (this) {
            if (scrapMemberColumn != null && scrapPostColumn != null) {
                return;
            }

            try {
                // H2 기준. 다른 DB 환경에서는 결과가 없을 수 있음 -> 그 경우 스크랩 기능만 OFF
                Query q =
                    em.createNativeQuery(
                        "select c.column_name "
                            + "from information_schema.columns c "
                            + "where c.table_name = 'BOARD_POST_SCRAPS'");

                @SuppressWarnings("unchecked")
                List<String> cols = q.getResultList();
                if (cols == null || cols.isEmpty()) {
                    return;
                }

                Set<String> upper = new HashSet<>();
                for (String c : cols) {
                    if (c != null) {
                        upper.add(c.toUpperCase());
                    }
                }

                // member 컬럼 후보
                if (upper.contains("MEMBER_ID")) {
                    scrapMemberColumn = "member_id";
                } else if (upper.contains("SCRAPPER_ID")) {
                    scrapMemberColumn = "scrapper_id";
                }

                // post 컬럼 후보
                if (upper.contains("POST_ID")) {
                    scrapPostColumn = "post_id";
                } else if (upper.contains("BOARD_POST_ID")) {
                    scrapPostColumn = "board_post_id";
                }
            } catch (PersistenceException e) {
                // 메타 조회 실패 시에도 목록은 살아야 해서 무시
            }
        }
    }

    private long countTotal(CriteriaBuilder cb, BoardPostListRequest request) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<BoardPost> countRoot = countQuery.from(BoardPost.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> predicates = buildPredicates(cb, countQuery, countRoot, request);
        countQuery.where(predicates.toArray(new Predicate[0]));

        return em.createQuery(countQuery).getSingleResult();
    }

    private List<Order> buildOrders(
        CriteriaBuilder cb, Root<BoardPost> root, BoardPostListRequest request) {

        // sort 값은 문자열로 들어온다고 가정하고, 안전하게 기본값(최신순) 유지
        String sort = request != null ? request.getSort() : null;
        if (sort == null || sort.isBlank()) {
            return List.of(cb.desc(root.get("createdAt")));
        }

        String s = sort.trim().toUpperCase();

        // - LATEST/NEW/CREATED_AT_DESC: 최신순
        // - OLDEST/CREATED_AT_ASC: 오래된순
        // - COMMENTED/LAST_COMMENTED_AT_DESC: 최근 댓글순
        // - VIEW/VIEW_COUNT_DESC: 조회수순
        // - SCRAP/SCRAP_COUNT_DESC: 스크랩순
        return switch (s) {
            case "OLDEST", "CREATED_AT_ASC", "ASC" -> List.of(cb.asc(root.get("createdAt")));
            case "COMMENTED", "LAST_COMMENTED", "LAST_COMMENTED_AT_DESC" ->
                List.of(cb.desc(root.get("lastCommentedAt")), cb.desc(root.get("createdAt")));
            case "VIEW", "VIEWS", "VIEW_COUNT_DESC" ->
                List.of(cb.desc(root.get("viewCount")), cb.desc(root.get("createdAt")));
            case "SCRAP", "SCRAPS", "SCRAP_COUNT_DESC" ->
                List.of(cb.desc(root.get("scrapCount")), cb.desc(root.get("createdAt")));
            default -> List.of(cb.desc(root.get("createdAt")));
        };
    }

    private <T> List<Predicate> buildPredicates(
        CriteriaBuilder cb, CriteriaQuery<T> query, Root<BoardPost> root, BoardPostListRequest request) {

        List<Predicate> predicates = new ArrayList<>();

        // 기본 정책: DELETED 제외
        predicates.add(cb.notEqual(root.get("postStatus"), PostStatus.DELETED));

        // status 정책
        // - ALL/전체/빈값 => OPEN + CLOSED (DELETED 제외만 적용)
        // - OPEN => OPEN
        // - CLOSED => CLOSED
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

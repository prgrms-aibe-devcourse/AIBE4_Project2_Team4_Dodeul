// src/main/java/org/aibe4/dodeul/domain/board/service/BoardPostService.java
package org.aibe4.dodeul.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.exception.BoardPolicyException;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostCreateRequest;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostListRequest;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostListResponse;
import org.aibe4.dodeul.domain.board.model.entity.BoardComment;
import org.aibe4.dodeul.domain.board.model.entity.BoardPost;
import org.aibe4.dodeul.domain.board.model.entity.BoardPostTagRelation;
import org.aibe4.dodeul.domain.board.model.enums.CommentStatus;
import org.aibe4.dodeul.domain.board.model.enums.PostStatus;
import org.aibe4.dodeul.domain.board.model.repository.BoardCommentRepository;
import org.aibe4.dodeul.domain.board.model.repository.BoardPostRepository;
import org.aibe4.dodeul.domain.board.model.repository.BoardPostTagRelationRepository;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;
import org.aibe4.dodeul.domain.common.repository.SkillTagRepository;
import org.aibe4.dodeul.domain.member.model.repository.MemberRepository;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardPostService {

    private final BoardPostRepository boardPostRepository;
    private final BoardPostTagRelationRepository boardPostTagRelationRepository;
    private final SkillTagRepository skillTagRepository;

    // 댓글 채택용
    private final BoardCommentRepository boardCommentRepository;

    // 닉네임 조회용(수정 금지 조건 만족: 레포 정의 그대로 사용)
    private final MemberRepository memberRepository;

    public Page<BoardPostListResponse> getPosts(
        BoardPostListRequest request, Long memberId, Pageable pageable) {
        BoardPostListRequest normalized = normalizeListRequest(request);

        validateListPolicy(memberId, normalized);

        Page<BoardPostListResponse> page = boardPostRepository.findPosts(normalized, memberId, pageable);

        // ✅ 목록 댓글 수 0 문제 해결: postIds 묶어서 한 번에 count 조회 후 주입
        List<Long> postIds =
            page.getContent().stream()
                .map(BoardPostListResponse::getPostId)
                .filter(id -> id != null)
                .toList();

        if (postIds.isEmpty()) {
            return page;
        }

        Map<Long, Long> commentCountMap = new HashMap<>();
        boardCommentRepository
            .findCommentCountsByPostIds(postIds, CommentStatus.DELETED)
            .forEach(row -> commentCountMap.put(row.getPostId(), row.getCnt()));

        return page.map(
            r ->
                BoardPostListResponse.builder()
                    .postId(r.getPostId())
                    .title(r.getTitle())
                    .postStatus(r.getPostStatus())
                    .viewCount(r.getViewCount())
                    .scrapCount(r.getScrapCount())
                    .skillTags(r.getSkillTags())
                    .commentCount(commentCountMap.getOrDefault(r.getPostId(), 0L))
                    .build());
    }

    // ✅ "전체" 선택 시 status 파라미터가 빈 문자열("")로 오는데,
    // 이걸 OPEN으로 강제하면 전체(OPEN+CLOSED) 조회가 안 됩니다.
    // - null/blank => null (필터 없음)
    // - 잘못된 값 => OPEN
    private BoardPostListRequest normalizeListRequest(BoardPostListRequest request) {
        if (request == null) {
            return BoardPostListRequest.builder().build();
        }

        String normalizedStatus = normalizeStatus(request.getStatus());

        return BoardPostListRequest.builder()
            .consultingTag(request.getConsultingTag())
            .tagIds(request.getTagIds())
            .status(normalizedStatus)
            .keyword(request.getKeyword())
            .sort(request.getSort())
            .build();
    }

    private String normalizeStatus(String raw) {
        if (raw == null) {
            return null;
        }

        String v = raw.trim();
        if (v.isEmpty()) {
            return null; // ✅ 전체
        }

        try {
            PostStatus parsed = PostStatus.valueOf(v.toUpperCase());
            if (parsed == PostStatus.DELETED) {
                return PostStatus.OPEN.name();
            }
            return parsed.name();
        } catch (IllegalArgumentException e) {
            return PostStatus.OPEN.name();
        }
    }

    @Transactional
    public Long createPost(Long memberId, BoardPostCreateRequest request) {
        if (memberId == null) {
            throw new BoardPolicyException(HttpStatus.UNAUTHORIZED, ErrorCode.ACCESS_DENIED, "로그인이 필요합니다.");
        }

        validateCreateRequest(request);

        BoardPost post =
            BoardPost.builder()
                .memberId(memberId)
                .title(request.getTitle().trim())
                .content(request.getContent().trim())
                .boardConsulting(request.getConsultingTag())
                .build();

        BoardPost saved = boardPostRepository.save(post);

        // 1) 체크박스/ID 기반 태그 연결
        List<Long> skillTagIds = request.getSkillTagIds();
        if (skillTagIds != null && !skillTagIds.isEmpty()) {
            Set<Long> distinct = new HashSet<>(skillTagIds);
            for (Long tagId : distinct) {
                SkillTag skillTag =
                    skillTagRepository
                        .findById(tagId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스킬태그가 포함되어 있습니다."));
                boardPostTagRelationRepository.save(BoardPostTagRelation.of(saved, skillTag));
            }
        }

        // 2) 이름 기반 태그(없으면 생성 후 연결)
        List<String> skillTagNames = request.getSkillTagNames();
        if (skillTagNames != null && !skillTagNames.isEmpty()) {
            Set<String> distinctNames = new HashSet<>();
            for (String raw : skillTagNames) {
                String name = normalizeName(raw);
                if (name.isBlank()) {
                    continue;
                }
                if (name.length() > 30) {
                    throw new IllegalArgumentException("스킬태그는 30자를 초과할 수 없습니다.");
                }
                distinctNames.add(name);
            }

            for (String name : distinctNames) {
                SkillTag tag =
                    skillTagRepository
                        .findByName(name)
                        .orElseGet(() -> skillTagRepository.save(new SkillTag(name)));
                boardPostTagRelationRepository.save(BoardPostTagRelation.of(saved, tag));
            }
        }

        return saved.getId();
    }

    @Transactional
    public void increaseViewCount(Long postId) {
        boardPostRepository.increaseViewCount(postId, PostStatus.DELETED);
    }

    /**
     * 댓글 채택
     *
     * <p>- 게시글 작성자만 가능
     * <p>- 최상위 댓글(root)만 채택 가능
     * <p>- 채택 성공 시 게시글 상태 CLOSED 전환
     */
    @Transactional
    public Long acceptComment(Long memberId, Long postId, Long commentId) {
        if (memberId == null) {
            throw new BoardPolicyException(HttpStatus.UNAUTHORIZED, ErrorCode.ACCESS_DENIED, "로그인이 필요합니다.");
        }

        BoardPost post =
            boardPostRepository
                .findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!memberId.equals(post.getMemberId())) {
            throw new BoardPolicyException(
                HttpStatus.FORBIDDEN, ErrorCode.ACCESS_DENIED, "채택은 게시글 작성자만 가능합니다.");
        }

        BoardComment comment =
            boardCommentRepository
                .findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (comment.getBoardPost() == null || !postId.equals(comment.getBoardPost().getId())) {
            throw new IllegalArgumentException("해당 게시글의 댓글만 채택할 수 있습니다.");
        }

        // 최상위 댓글만 채택 가능(depth=1)
        if (comment.getParentComment() != null) {
            throw new IllegalArgumentException("대댓글은 채택할 수 없습니다.");
        }

        post.acceptComment(comment); // 내부에서 "이미 채택됨" 체크 + CLOSED 전환
        return comment.getId();
    }

    private String normalizeName(String value) {
        return value == null ? "" : value.trim();
    }

    private void validateListPolicy(Long memberId, BoardPostListRequest request) {
        if (isDefaultListRequest(request)) {
            return;
        }

        if (memberId == null) {
            throw new BoardPolicyException(
                HttpStatus.UNAUTHORIZED, ErrorCode.ACCESS_DENIED, "검색/필터 기능은 로그인 후 이용 가능합니다.");
        }
    }

    private boolean isDefaultListRequest(BoardPostListRequest request) {
        if (request == null) {
            return true;
        }

        boolean hasConsultingTag = request.getConsultingTag() != null;
        boolean hasTagIds = request.getTagIds() != null && !request.getTagIds().isEmpty();

        boolean hasStatus = request.getStatus() != null && !request.getStatus().isBlank();
        boolean hasKeyword = request.getKeyword() != null && !request.getKeyword().isBlank();

        boolean hasSort =
            request.getSort() != null && !request.getSort().isBlank() && !"LATEST".equalsIgnoreCase(request.getSort());

        return !(hasConsultingTag || hasTagIds || hasStatus || hasKeyword || hasSort);
    }

    private void validateCreateRequest(BoardPostCreateRequest request) {
        if (request.getConsultingTag() == null) {
            throw new IllegalArgumentException("상담분야는 필수입니다.");
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("제목은 공백일 수 없습니다.");
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("내용은 공백일 수 없습니다.");
        }

        String title = request.getTitle().trim();
        String content = request.getContent().trim();

        if (title.length() < 2 || title.length() > 100) {
            throw new IllegalArgumentException("제목은 2자 이상 100자 이하여야 합니다.");
        }
        if (content.length() < 10) {
            throw new IllegalArgumentException("내용은 10자 이상이어야 합니다.");
        }
    }
}

// src/main/java/org/aibe4/dodeul/domain/board/service/BoardPostService.java
package org.aibe4.dodeul.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.exception.BoardPolicyException;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostCreateRequest;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostListRequest;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostListResponse;
import org.aibe4.dodeul.domain.board.model.entity.BoardPost;
import org.aibe4.dodeul.domain.board.model.entity.BoardPostTagRelation;
import org.aibe4.dodeul.domain.board.model.repository.BoardPostRepository;
import org.aibe4.dodeul.domain.board.model.repository.BoardPostTagRelationRepository;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;
import org.aibe4.dodeul.domain.common.repository.SkillTagRepository;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardPostService {

    private final BoardPostRepository boardPostRepository;
    private final BoardPostTagRelationRepository boardPostTagRelationRepository;
    private final SkillTagRepository skillTagRepository;

    public Page<BoardPostListResponse> getPosts(
        BoardPostListRequest request, Long memberId, Pageable pageable) {
        validateListPolicy(memberId, request);
        return boardPostRepository.findPosts(request, memberId, pageable);
    }

    @Transactional
    public Long createPost(Long memberId, BoardPostCreateRequest request) {
        if (memberId == null) {
            throw new BoardPolicyException(
                HttpStatus.UNAUTHORIZED, ErrorCode.ACCESS_DENIED, "로그인이 필요합니다.");
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
                        .orElseThrow(
                            () -> new IllegalArgumentException("존재하지 않는 스킬태그가 포함되어 있습니다."));
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

    private String normalizeName(String value) {
        return value == null ? "" : value.trim();
    }

    private void validateListPolicy(Long memberId, BoardPostListRequest request) {
        if (isDefaultListRequest(request)) {
            return;
        }

        if (memberId == null) {
            throw new BoardPolicyException(
                HttpStatus.UNAUTHORIZED,
                ErrorCode.ACCESS_DENIED,
                "검색/필터 기능은 로그인 후 이용 가능합니다.");
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
            request.getSort() != null
                && !request.getSort().isBlank()
                && !"LATEST".equalsIgnoreCase(request.getSort());

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

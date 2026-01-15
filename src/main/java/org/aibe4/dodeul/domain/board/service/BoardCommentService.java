// src/main/java/org/aibe4/dodeul/domain/board/service/BoardCommentService.java
package org.aibe4.dodeul.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.exception.BoardPolicyException;
import org.aibe4.dodeul.domain.board.model.dto.comment.BoardCommentCreateRequest;
import org.aibe4.dodeul.domain.board.model.dto.comment.BoardCommentItemResponse;
import org.aibe4.dodeul.domain.board.model.dto.comment.BoardCommentListResponse;
import org.aibe4.dodeul.domain.board.model.dto.comment.BoardCommentUpdateRequest;
import org.aibe4.dodeul.domain.board.model.entity.BoardComment;
import org.aibe4.dodeul.domain.board.model.entity.BoardCommentLike;
import org.aibe4.dodeul.domain.board.model.entity.BoardPost;
import org.aibe4.dodeul.domain.board.model.enums.CommentStatus;
import org.aibe4.dodeul.domain.board.model.enums.PostStatus;
import org.aibe4.dodeul.domain.board.model.repository.BoardCommentLikeRepository;
import org.aibe4.dodeul.domain.board.model.repository.BoardCommentRepository;
import org.aibe4.dodeul.domain.board.model.repository.BoardPostRepository;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.entity.MenteeProfile;
import org.aibe4.dodeul.domain.member.model.entity.MentorProfile;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.aibe4.dodeul.domain.member.model.repository.MemberRepository;
import org.aibe4.dodeul.domain.member.model.repository.MenteeProfileRepository;
import org.aibe4.dodeul.domain.member.model.repository.MentorProfileRepository;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardCommentService {

    private static final String DELETED_CONTENT = "삭제된 댓글입니다.";

    private final BoardPostRepository boardPostRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final BoardCommentLikeRepository boardCommentLikeRepository;
    private final MemberRepository memberRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final MenteeProfileRepository menteeProfileRepository;

    public BoardCommentListResponse getComments(Long postId, Long memberId) {
        BoardPost post =
            boardPostRepository
                .findById(postId)
                .orElseThrow(
                    () ->
                        new BoardPolicyException(
                            ErrorCode.RESOURCE_NOT_FOUND, "게시글을 찾을 수 없습니다."));

        Long postAuthorId = extractPostAuthorId(post);

        List<BoardComment> comments = boardCommentRepository.findAllByPostId(postId);
        Long acceptedId = boardPostRepository.findAcceptedCommentId(postId);

        if (comments.isEmpty()) {
            return BoardCommentListResponse.of(postId, acceptedId, Collections.emptyList());
        }

        Set<Long> memberIds = new HashSet<>();
        memberIds.add(postAuthorId);
        for (BoardComment c : comments) {
            if (c.getMemberId() != null) {
                memberIds.add(c.getMemberId());
            }
        }
        Map<Long, Member> memberMap = loadMemberMap(memberIds);

        // 프로필 이미지 조회 (N+1 방지)
        Map<Long, String> profileImageMap = loadProfileImageMap(memberIds, memberMap);

        List<Long> commentIds = comments.stream().map(BoardComment::getId).toList();
        Set<Long> likedIds = Collections.emptySet();
        if (memberId != null && !commentIds.isEmpty()) {
            likedIds = new HashSet<>(boardCommentLikeRepository.findLikedCommentIds(memberId, commentIds));
        }

        boolean viewerIsPostAuthor = memberId != null && Objects.equals(memberId, postAuthorId);
        boolean postIsOpen = post.getPostStatus() == PostStatus.OPEN;

        Map<Long, BoardCommentItemResponse> rootMap = new LinkedHashMap<>();
        for (BoardComment c : comments) {
            boolean isRoot = c.getParentComment() == null;
            if (isRoot) {
                rootMap.put(
                    c.getId(),
                    toItemResponse(
                        c,
                        memberId,
                        postAuthorId,
                        memberMap,
                        profileImageMap,
                        acceptedId,
                        likedIds.contains(c.getId()),
                        true,
                        viewerIsPostAuthor,
                        postIsOpen));
            }
        }

        for (BoardComment c : comments) {
            if (c.getParentComment() == null) {
                continue;
            }
            Long rootId = c.getRootComment() != null ? c.getRootComment().getId() : null;
            if (rootId == null) {
                continue;
            }
            BoardCommentItemResponse root = rootMap.get(rootId);
            if (root == null) {
                continue;
            }
            root.getChildren()
                .add(
                    toItemResponse(
                        c,
                        memberId,
                        postAuthorId,
                        memberMap,
                        profileImageMap,
                        acceptedId,
                        likedIds.contains(c.getId()),
                        false,
                        viewerIsPostAuthor,
                        postIsOpen));
        }

        return BoardCommentListResponse.of(postId, acceptedId, new ArrayList<>(rootMap.values()));
    }

    @Transactional
    public void createComment(Long postId, Long memberId, BoardCommentCreateRequest request) {
        if (memberId == null) {
            throw new BoardPolicyException(ErrorCode.UNAUTHORIZED_ACCESS, "로그인이 필요합니다.");
        }

        BoardPost post =
            boardPostRepository
                .findById(postId)
                .orElseThrow(
                    () ->
                        new BoardPolicyException(
                            ErrorCode.RESOURCE_NOT_FOUND, "게시글을 찾을 수 없습니다."));

        String content = normalize(request.getContent());
        if (content.isBlank()) {
            throw new BoardPolicyException(ErrorCode.INVALID_INPUT_VALUE, "내용은 공백일 수 없습니다.");
        }

        BoardComment parent = null;
        BoardComment root = null;

        if (request.getParentCommentId() != null) {
            parent =
                boardCommentRepository
                    .findById(request.getParentCommentId())
                    .orElseThrow(
                        () ->
                            new BoardPolicyException(
                                ErrorCode.RESOURCE_NOT_FOUND, "부모 댓글을 찾을 수 없습니다."));

            if (!Objects.equals(parent.getBoardPost().getId(), postId)) {
                throw new BoardPolicyException(
                    ErrorCode.INVALID_INPUT_VALUE, "부모 댓글이 해당 게시글에 속하지 않습니다.");
            }
            if (parent.getCommentStatus() == CommentStatus.DELETED) {
                throw new BoardPolicyException(
                    ErrorCode.INVALID_INPUT_VALUE, "삭제된 댓글에는 답글을 작성할 수 없습니다.");
            }
            if (parent.getParentComment() != null) {
                throw new BoardPolicyException(ErrorCode.INVALID_INPUT_VALUE, "대댓글에는 답글을 작성할 수 없습니다.");
            }

            root = parent;
        }

        BoardComment saved =
            boardCommentRepository.save(
                BoardComment.builder()
                    .memberId(memberId)
                    .boardPost(post)
                    .parentComment(parent)
                    .rootComment(root)
                    .content(content)
                    .build());

        if (root == null) {
            boardCommentRepository.updateRootCommentId(saved.getId(), saved.getId());
        }

        // ✅ 댓글 수는 목록/상세에서 실집계로 맞추므로, 엔티티 캐시 필드는 여기서 갱신하지 않는다.
    }

    @Transactional
    public void updateComment(Long commentId, Long memberId, BoardCommentUpdateRequest request) {
        if (memberId == null) {
            throw new BoardPolicyException(ErrorCode.UNAUTHORIZED_ACCESS, "로그인이 필요합니다.");
        }

        BoardComment comment =
            boardCommentRepository
                .findById(commentId)
                .orElseThrow(
                    () -> new BoardPolicyException(ErrorCode.RESOURCE_NOT_FOUND, "댓글을 찾을 수 없습니다."));

        if (!Objects.equals(comment.getMemberId(), memberId)) {
            throw new BoardPolicyException(ErrorCode.ACCESS_DENIED, "작성자만 수정할 수 있습니다.");
        }

        String content = normalize(request.getContent());
        if (content.isBlank()) {
            throw new BoardPolicyException(ErrorCode.INVALID_INPUT_VALUE, "내용은 공백일 수 없습니다.");
        }

        comment.update(content);
    }

    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        if (memberId == null) {
            throw new BoardPolicyException(ErrorCode.UNAUTHORIZED_ACCESS, "로그인이 필요합니다.");
        }

        BoardComment comment =
            boardCommentRepository
                .findById(commentId)
                .orElseThrow(
                    () -> new BoardPolicyException(ErrorCode.RESOURCE_NOT_FOUND, "댓글을 찾을 수 없습니다."));

        if (!Objects.equals(comment.getMemberId(), memberId)) {
            throw new BoardPolicyException(ErrorCode.ACCESS_DENIED, "작성자만 삭제할 수 있습니다.");
        }

        comment.delete();
    }

    @Transactional
    public void toggleLike(Long commentId, Long memberId) {
        if (memberId == null) {
            throw new BoardPolicyException(ErrorCode.UNAUTHORIZED_ACCESS, "로그인이 필요합니다.");
        }

        BoardComment comment =
            boardCommentRepository
                .findById(commentId)
                .orElseThrow(
                    () -> new BoardPolicyException(ErrorCode.RESOURCE_NOT_FOUND, "댓글을 찾을 수 없습니다."));

        Optional<BoardCommentLike> existing =
            boardCommentLikeRepository.findByBoardCommentIdAndMemberId(commentId, memberId);

        if (existing.isPresent()) {
            boardCommentLikeRepository.delete(existing.get());
            comment.decreaseLikeCount();
            return;
        }

        boardCommentLikeRepository.save(
            BoardCommentLike.builder().boardComment(comment).memberId(memberId).build());
        comment.increaseLikeCount();
    }

    private BoardCommentItemResponse toItemResponse(
        BoardComment c,
        Long viewerMemberId,
        Long postAuthorId,
        Map<Long, Member> memberMap,
        Map<Long, String> profileImageMap,
        Long acceptedId,
        boolean likedByMe,
        boolean isRoot,
        boolean viewerIsPostAuthor,
        boolean postIsOpen) {

        boolean deleted = c.getCommentStatus() == CommentStatus.DELETED;
        String content = deleted ? DELETED_CONTENT : c.getContent();

        Long parentId = c.getParentComment() != null ? c.getParentComment().getId() : null;
        Long rootId = c.getRootComment() != null ? c.getRootComment().getId() : null;

        Long commentAuthorId = c.getMemberId();
        Member author = commentAuthorId == null ? null : memberMap.get(commentAuthorId);
        String nickname =
            author != null && author.getNickname() != null && !author.getNickname().isBlank()
                ? author.getNickname()
                : "익명";

        String roleTag = resolveRoleTag(commentAuthorId, postAuthorId, author);

        // 프로필 이미지 URL 조회
        String profileImageUrl = commentAuthorId != null ? profileImageMap.get(commentAuthorId) : null;

        boolean isAccepted = acceptedId != null && Objects.equals(acceptedId, c.getId());

        boolean mine =
            viewerMemberId != null
                && commentAuthorId != null
                && Objects.equals(viewerMemberId, commentAuthorId);

        boolean canEdit = mine && !deleted;
        boolean canDelete = mine && !deleted;
        boolean canReply = isRoot && viewerMemberId != null && !deleted;

        boolean canAccept =
            isRoot
                && viewerIsPostAuthor
                && postIsOpen
                && !isAccepted
                && !deleted
                && commentAuthorId != null
                && !Objects.equals(commentAuthorId, postAuthorId);

        return BoardCommentItemResponse.builder()
            .commentId(c.getId())
            .postId(c.getBoardPost().getId())
            .parentCommentId(parentId)
            .rootCommentId(rootId != null ? rootId : (isRoot ? c.getId() : null))
            .depth(isRoot ? 1 : 2)
            .authorDisplayName(nickname)
            .authorNickname(nickname)
            .authorRoleTag(roleTag)
            .authorProfileImageUrl(profileImageUrl)
            .content(content)
            .commentStatus(c.getCommentStatus().name())
            .likeCount(c.getLikeCount() != null ? c.getLikeCount() : 0)
            .accepted(isAccepted)
            .likedByMe(likedByMe)
            .canAccept(canAccept)
            .mine(mine)
            .canEdit(canEdit)
            .canDelete(canDelete)
            .canReply(canReply)
            .createdAt(c.getCreatedAt())
            .updatedAt(c.getUpdatedAt())
            .children(new ArrayList<>())
            .build();
    }

    private Map<Long, Member> loadMemberMap(Set<Long> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Member> members = memberRepository.findAllById(memberIds);
        Map<Long, Member> map = new HashMap<>();
        for (Member m : members) {
            map.put(m.getId(), m);
        }
        return map;
    }

    /**
     * 프로필 이미지 일괄 조회 (N+1 방지)
     *
     * @param memberIds 조회할 회원 ID 목록
     * @param memberMap 이미 조회된 회원 정보 맵
     * @return memberId -> profileImageUrl 맵
     */
    private Map<Long, String> loadProfileImageMap(Set<Long> memberIds, Map<Long, Member> memberMap) {
        if (memberIds == null || memberIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, String> profileImageMap = new HashMap<>();

        // Member의 Role에 따라 적절한 프로필에서 이미지 URL 조회
        Set<Long> mentorIds = new HashSet<>();
        Set<Long> menteeIds = new HashSet<>();

        for (Long memberId : memberIds) {
            Member member = memberMap.get(memberId);
            if (member == null || member.getRole() == null) {
                continue;
            }

            if (member.getRole() == Role.MENTOR) {
                mentorIds.add(memberId);
            } else if (member.getRole() == Role.MENTEE) {
                menteeIds.add(memberId);
            }
        }

        // 멘토 프로필 이미지 조회
        if (!mentorIds.isEmpty()) {
            List<MentorProfile> mentorProfiles = mentorProfileRepository.findAllById(mentorIds);
            for (MentorProfile profile : mentorProfiles) {
                if (profile.getProfileUrl() != null && !profile.getProfileUrl().isBlank()) {
                    profileImageMap.put(profile.getId(), profile.getProfileUrl());
                }
            }
        }

        // 멘티 프로필 이미지 조회
        if (!menteeIds.isEmpty()) {
            List<MenteeProfile> menteeProfiles = menteeProfileRepository.findAllById(menteeIds);
            for (MenteeProfile profile : menteeProfiles) {
                if (profile.getProfileUrl() != null && !profile.getProfileUrl().isBlank()) {
                    profileImageMap.put(profile.getId(), profile.getProfileUrl());
                }
            }
        }

        return profileImageMap;
    }

    private Long extractPostAuthorId(BoardPost post) {
        Long id = post.getMemberId();
        if (id == null) {
            throw new BoardPolicyException(ErrorCode.INTERNAL_SERVER_ERROR, "게시글 작성자 정보를 가져올 수 없습니다.");
        }
        return id;
    }

    private String resolveRoleTag(Long commentAuthorId, Long postAuthorId, Member author) {
        if (commentAuthorId != null && Objects.equals(commentAuthorId, postAuthorId)) {
            return "작성자";
        }
        if (author == null || author.getRole() == null) {
            return "멘티";
        }
        String roleName = author.getRole().name();
        if ("MENTOR".equalsIgnoreCase(roleName)) {
            return "멘토";
        }
        return "멘티";
    }

    private String normalize(String v) {
        return v == null ? "" : v.trim();
    }
}

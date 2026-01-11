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
import org.aibe4.dodeul.domain.member.model.repository.MemberRepository;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional(readOnly = true)
public class BoardCommentService {

    private static final String DELETED_CONTENT = "삭제된 댓글입니다";

    private final BoardPostRepository boardPostRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final BoardCommentLikeRepository boardCommentLikeRepository;

    private final MemberRepository memberRepository;

    public BoardCommentListResponse getComments(Long postId, Long memberId) {
        BoardPost post =
            boardPostRepository
                .findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

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
            throw new BoardPolicyException(
                HttpStatus.UNAUTHORIZED, ErrorCode.ACCESS_DENIED, "로그인이 필요합니다.");
        }

        BoardPost post =
            boardPostRepository
                .findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        String content = normalize(request.getContent());
        if (content.isBlank()) {
            throw new IllegalArgumentException("내용은 공백일 수 없습니다.");
        }

        BoardComment parent = null;
        BoardComment root = null;

        if (request.getParentCommentId() != null) {
            parent =
                boardCommentRepository
                    .findById(request.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));

            if (!Objects.equals(parent.getBoardPost().getId(), postId)) {
                throw new IllegalArgumentException("부모 댓글이 해당 게시글에 속하지 않습니다.");
            }
            if (parent.getCommentStatus() == CommentStatus.DELETED) {
                throw new IllegalStateException("삭제된 댓글에는 답글을 작성할 수 없습니다");
            }
            if (parent.getParentComment() != null) {
                throw new IllegalArgumentException("대댓글에는 답글을 작성할 수 없습니다.");
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

        post.increaseCommentCount();
    }

    @Transactional
    public void updateComment(Long commentId, Long memberId, BoardCommentUpdateRequest request) {
        if (memberId == null) {
            throw new BoardPolicyException(
                HttpStatus.UNAUTHORIZED, ErrorCode.ACCESS_DENIED, "로그인이 필요합니다.");
        }

        BoardComment comment =
            boardCommentRepository
                .findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!Objects.equals(comment.getMemberId(), memberId)) {
            throw new IllegalStateException("작성자만 수정할 수 있습니다");
        }

        String content = normalize(request.getContent());
        if (content.isBlank()) {
            throw new IllegalArgumentException("내용은 공백일 수 없습니다.");
        }

        comment.update(content);
    }

    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        if (memberId == null) {
            throw new BoardPolicyException(
                HttpStatus.UNAUTHORIZED, ErrorCode.ACCESS_DENIED, "로그인이 필요합니다.");
        }

        BoardComment comment =
            boardCommentRepository
                .findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!Objects.equals(comment.getMemberId(), memberId)) {
            throw new IllegalStateException("작성자만 삭제할 수 있습니다");
        }

        comment.delete();
    }

    @Transactional
    public void toggleLike(Long commentId, Long memberId) {
        if (memberId == null) {
            throw new BoardPolicyException(
                HttpStatus.UNAUTHORIZED, ErrorCode.ACCESS_DENIED, "로그인이 필요합니다.");
        }

        BoardComment comment =
            boardCommentRepository
                .findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

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
        String nickname = author != null && author.getNickname() != null ? author.getNickname() : "익명";

        String roleTag = resolveRoleTag(commentAuthorId, postAuthorId, author);

        boolean isAccepted = acceptedId != null && Objects.equals(acceptedId, c.getId());

        boolean mine = viewerMemberId != null && commentAuthorId != null && Objects.equals(viewerMemberId, commentAuthorId);
        boolean canEdit = mine && !deleted;
        boolean canDelete = mine && !deleted;
        boolean canReply = isRoot && viewerMemberId != null && !deleted; // CLOSED여도 답글 가능 정책

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

    private Long extractPostAuthorId(BoardPost post) {
        Long id = post.getMemberId();
        if (id == null) {
            throw new IllegalStateException("게시글 작성자 정보를 가져올 수 없습니다.");
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

// src/main/java/org/aibe4/dodeul/domain/board/service/BoardPostScrapService.java
package org.aibe4.dodeul.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostScrapStatusResponse;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostScrapToggleResponse;
import org.aibe4.dodeul.domain.board.model.dto.response.MyScrapItemResponse;
import org.aibe4.dodeul.domain.board.model.dto.response.MyScrapListResponse;
import org.aibe4.dodeul.domain.board.model.entity.BoardPost;
import org.aibe4.dodeul.domain.board.model.entity.BoardPostScrap;
import org.aibe4.dodeul.domain.board.model.enums.PostStatus;
import org.aibe4.dodeul.domain.board.model.repository.BoardPostRepository;
import org.aibe4.dodeul.domain.board.model.repository.BoardPostScrapRepository;
import org.aibe4.dodeul.domain.common.model.entity.SkillTag;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardPostScrapService {

    private static final DateTimeFormatter SUBTEXT_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final BoardPostRepository boardPostRepository;
    private final BoardPostScrapRepository boardPostScrapRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public BoardPostScrapToggleResponse toggle(Long memberId, Long postId) {
        BoardPost post = getActivePostOrThrow(postId);

        boolean exists = boardPostScrapRepository.existsByBoardPostIdAndMemberId(postId, memberId);
        if (exists) {
            boardPostScrapRepository.deleteByBoardPostIdAndMemberId(postId, memberId);
            post.decreaseScrapCount();
        } else {
            BoardPostScrap scrap = BoardPostScrap.builder().boardPost(post).memberId(memberId).build();
            boardPostScrapRepository.save(scrap);
            post.increaseScrapCount();
        }

        long scrapCount = boardPostScrapRepository.countByBoardPostId(postId);
        boolean scrappedByMe = !exists;

        return BoardPostScrapToggleResponse.of(postId, scrappedByMe, scrapCount);
    }

    @Transactional
    public BoardPostScrapToggleResponse delete(Long memberId, Long postId) {
        BoardPost post = getActivePostOrThrow(postId);

        boolean exists = boardPostScrapRepository.existsByBoardPostIdAndMemberId(postId, memberId);
        if (!exists) {
            long scrapCount = boardPostScrapRepository.countByBoardPostId(postId);
            return BoardPostScrapToggleResponse.of(postId, false, scrapCount);
        }

        boardPostScrapRepository.deleteByBoardPostIdAndMemberId(postId, memberId);
        post.decreaseScrapCount();

        long scrapCount = boardPostScrapRepository.countByBoardPostId(postId);
        return BoardPostScrapToggleResponse.of(postId, false, scrapCount);
    }

    public BoardPostScrapStatusResponse getStatus(Long memberIdOrNull, Long postId) {
        getActivePostOrThrow(postId);

        long scrapCount = boardPostScrapRepository.countByBoardPostId(postId);
        boolean scrappedByMe =
            memberIdOrNull != null
                && boardPostScrapRepository.existsByBoardPostIdAndMemberId(postId, memberIdOrNull);

        return BoardPostScrapStatusResponse.of(postId, scrappedByMe, scrapCount);
    }

    public MyScrapListResponse getMyScraps(Long memberId) {
        List<BoardPostScrap> scraps = boardPostScrapRepository.findMyScrapsWithPost(memberId);

        List<MyScrapItemResponse> items =
            scraps.stream()
                .filter(s -> s.getBoardPost().getPostStatus() != PostStatus.DELETED) // 삭제된 게시글 필터링
                .map(
                    s -> {
                        BoardPost p = s.getBoardPost();
                        String date =
                            p.getCreatedAt() == null
                                ? "-"
                                : p.getCreatedAt().toLocalDate().format(SUBTEXT_DATE);

                        // 작성자 닉네임 조회
                        String authorName = "익명";
                        if (p.getMemberId() != null) {
                            authorName = memberRepository.findById(p.getMemberId())
                                .map(Member::getNickname)
                                .orElse("익명");
                        }

                        // 스킬 태그 목록
                        List<String> tagNames = p.getSkillTags().stream()
                            .map(SkillTag::getName)
                            .toList();

                        String subText = String.format("작성자: %s · %s", authorName, date);
                        return MyScrapItemResponse.of(p.getId(), "post", p.getTitle(), subText, tagNames);
                    })
                .toList();

        return MyScrapListResponse.of(items);
    }

    private BoardPost getActivePostOrThrow(Long postId) {
        BoardPost post =
            boardPostRepository
                .findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (post.getPostStatus() == PostStatus.DELETED) {
            throw new IllegalStateException("삭제된 게시글입니다.");
        }

        return post;
    }
}

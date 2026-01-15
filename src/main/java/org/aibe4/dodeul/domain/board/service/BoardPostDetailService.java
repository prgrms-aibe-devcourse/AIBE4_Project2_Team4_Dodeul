package org.aibe4.dodeul.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.exception.BoardPolicyException;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostDetailResponse;
import org.aibe4.dodeul.domain.board.model.entity.BoardPost;
import org.aibe4.dodeul.domain.board.model.enums.PostStatus;
import org.aibe4.dodeul.domain.board.model.repository.BoardPostRepository;
import org.aibe4.dodeul.domain.common.model.entity.CommonFile;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.repository.MemberRepository;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardPostDetailService {

    private final BoardPostRepository boardPostRepository;
    private final MemberRepository memberRepository;
    private final BoardPostFileService boardPostFileService;

    public BoardPostDetailResponse getDetail(Long postId, Long viewerMemberId) {
        BoardPost post = boardPostRepository
            .findDetailById(postId)
            .orElseThrow(() ->
                new BoardPolicyException(ErrorCode.RESOURCE_NOT_FOUND, "게시글을 찾을 수 없습니다."));

        if (post.getPostStatus() == PostStatus.DELETED) {
            throw new BoardPolicyException(ErrorCode.RESOURCE_NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }

        String authorDisplayName = resolveAuthorDisplayName(post.getMemberId());

        List<CommonFile> files = boardPostFileService.getFiles(postId);

        boolean mine = viewerMemberId != null && Objects.equals(viewerMemberId, post.getMemberId());

        // 스크랩 여부는 추후 별도 API로 분리 가능 (현재는 false 고정)
        return BoardPostDetailResponse.from(post, authorDisplayName, false, mine, files);
    }

    private String resolveAuthorDisplayName(Long memberId) {
        if (memberId == null) {
            return "작성자";
        }

        return memberRepository.findById(memberId)
            .map(Member::getNickname)
            .filter(nickname -> nickname != null && !nickname.isBlank())
            .orElse("작성자");
    }
}

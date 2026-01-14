package org.aibe4.dodeul.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.model.dto.response.BoardPostDetailResponse;
import org.aibe4.dodeul.domain.board.model.entity.BoardPost;
import org.aibe4.dodeul.domain.board.model.repository.BoardPostRepository;
import org.aibe4.dodeul.domain.common.model.entity.CommonFile;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardPostDetailService {

    private final BoardPostRepository boardPostRepository;
    private final MemberRepository memberRepository;
    private final BoardPostFileService boardPostFileService;

    public BoardPostDetailResponse getDetail(Long postId) {
        BoardPost post =
            boardPostRepository
                .findDetailById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        String authorDisplayName = resolveAuthorDisplayName(post.getMemberId());
        List<CommonFile> files = boardPostFileService.getFiles(postId);

        // 인증 연동 전까지는 false 고정
        return BoardPostDetailResponse.from(post, authorDisplayName, false, files);
    }

    private String resolveAuthorDisplayName(Long memberId) {
        if (memberId == null) {
            return "작성자";
        }

        Member member = memberRepository.findById(memberId).orElse(null);

        if (member == null || member.getNickname() == null || member.getNickname().isBlank()) {
            return "작성자";
        }

        return member.getNickname();
    }
}

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
        // ✅ 최적화: @EntityGraph로 태그까지 한번에 조회
        BoardPost post = boardPostRepository
            .findDetailById(postId)
            .orElseThrow(() ->
                new BoardPolicyException(ErrorCode.RESOURCE_NOT_FOUND, "게시글을 찾을 수 없습니다."));

        if (post.getPostStatus() == PostStatus.DELETED) {
            throw new BoardPolicyException(ErrorCode.RESOURCE_NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }

        // ✅ 최적화: 작성자 정보 조회 - 캐싱 가능한 부분
        String authorDisplayName = resolveAuthorDisplayName(post.getMemberId());

        // ✅ 최적화: 파일 목록 조회 - 이미 최적화되어 있음 (단일 쿼리)
        List<CommonFile> files = boardPostFileService.getFiles(postId);

        // ✅ 최적화: 권한 체크 로직 간소화
        boolean mine = viewerMemberId != null && Objects.equals(viewerMemberId, post.getMemberId());

        // 스크랩 여부는 추후 별도 API로 분리 가능 (현재는 false 고정)
        return BoardPostDetailResponse.from(post, authorDisplayName, false, mine, files);
    }

    private String resolveAuthorDisplayName(Long memberId) {
        if (memberId == null) {
            return "작성자";
        }

        // ✅ 최적화: Optional 체이닝으로 간소화
        return memberRepository.findById(memberId)
            .map(Member::getNickname)
            .filter(nickname -> nickname != null && !nickname.isBlank())
            .orElse("작성자");
    }
}

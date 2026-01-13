package org.aibe4.dodeul.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.board.exception.BoardPolicyException;
import org.aibe4.dodeul.domain.board.model.dto.request.BoardPostFileCreateRequest;
import org.aibe4.dodeul.domain.board.model.entity.BoardPost;
import org.aibe4.dodeul.domain.board.model.repository.BoardPostRepository;
import org.aibe4.dodeul.domain.common.model.entity.CommonFile;
import org.aibe4.dodeul.domain.common.model.enums.FileDomain;
import org.aibe4.dodeul.domain.common.repository.CommonFileRepository;
import org.aibe4.dodeul.global.response.enums.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardPostFileService {

    private static final int MAX_FILES_PER_POST = 5;

    private final BoardPostRepository boardPostRepository;
    private final CommonFileRepository commonFileRepository;

    @Transactional
    public List<CommonFile> addFiles(Long memberId, Long postId, BoardPostFileCreateRequest request) {
        // ↑ static 제거!
        if (memberId == null) {
            throw new BoardPolicyException(ErrorCode.ACCESS_DENIED, "로그인이 필요합니다.");
        }

        BoardPost post =
            boardPostRepository
                .findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!memberId.equals(post.getMemberId())) {
            throw new BoardPolicyException(ErrorCode.ACCESS_DENIED, "파일 등록은 작성자만 가능합니다.");
        }

        List<CommonFile> existing =
            commonFileRepository.findAllByDomainAndMessageIdOrderByCreatedAtAsc(FileDomain.BOARD_POST, postId);

        int incoming = request.getFiles() == null ? 0 : request.getFiles().size();
        if (existing.size() + incoming > MAX_FILES_PER_POST) {
            throw new IllegalArgumentException("파일은 게시글당 최대 5개까지 등록할 수 있습니다.");
        }

        List<CommonFile> saved = new ArrayList<>();
        for (BoardPostFileCreateRequest.Item item : request.getFiles()) {
            CommonFile file =
                CommonFile.ofBoardPost(
                    postId,
                    item.getFileUrl(),
                    item.getOriginFileName(),
                    item.getContentType(),
                    item.getFileSize()
                );
            saved.add(commonFileRepository.save(file));
        }
        return saved;
    }

    @Transactional
    public void deleteFile(Long memberId, Long postId, Long fileId) {
        if (memberId == null) {
            throw new BoardPolicyException(ErrorCode.ACCESS_DENIED, "로그인이 필요합니다.");
        }

        BoardPost post =
            boardPostRepository
                .findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!memberId.equals(post.getMemberId())) {
            throw new BoardPolicyException(ErrorCode.ACCESS_DENIED, "파일 삭제는 작성자만 가능합니다.");
        }

        CommonFile file =
            commonFileRepository
                .findByIdAndDomainAndMessageId(fileId, FileDomain.BOARD_POST, postId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        commonFileRepository.delete(file);
    }

    public List<CommonFile> getFiles(Long postId) {
        return commonFileRepository.findAllByDomainAndMessageIdOrderByCreatedAtAsc(FileDomain.BOARD_POST, postId);
    }
}

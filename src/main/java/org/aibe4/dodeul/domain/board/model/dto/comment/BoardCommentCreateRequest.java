package org.aibe4.dodeul.domain.board.model.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardCommentCreateRequest {

    @NotBlank(message = "내용은 공백일 수 없습니다.")
    private String content;

    // null이면 최상위 댓글, 값이 있으면 대댓글(부모는 depth=1만 허용)
    private Long parentCommentId;
}

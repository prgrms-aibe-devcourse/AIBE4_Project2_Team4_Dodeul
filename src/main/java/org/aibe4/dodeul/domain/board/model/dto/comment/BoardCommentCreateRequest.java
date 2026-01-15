package org.aibe4.dodeul.domain.board.model.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "댓글 작성 요청")
public class BoardCommentCreateRequest {

    @NotBlank(message = "내용은 공백일 수 없습니다.")
    @Schema(description = "댓글 내용", example = "좋은 정보 감사합니다!")
    private String content;

    @Schema(description = "부모 댓글 ID (대댓글인 경우 필수, 최상위 댓글이면 null)", example = "1")
    private Long parentCommentId;
}

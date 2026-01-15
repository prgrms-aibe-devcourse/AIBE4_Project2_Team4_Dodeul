package org.aibe4.dodeul.domain.board.model.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "댓글 수정 요청")
public class BoardCommentUpdateRequest {

    @NotBlank(message = "내용은 공백일 수 없습니다.")
    @Schema(description = "수정할 댓글 내용", example = "내용을 수정했습니다.")
    private String content;
}

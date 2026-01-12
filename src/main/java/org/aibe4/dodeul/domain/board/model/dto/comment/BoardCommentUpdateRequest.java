package org.aibe4.dodeul.domain.board.model.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardCommentUpdateRequest {

    @NotBlank(message = "내용은 공백일 수 없습니다.")
    private String content;
}

// src/main/java/org/aibe4/dodeul/domain/board/model/dto/request/BoardPostAcceptRequest.java
package org.aibe4.dodeul.domain.board.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardPostAcceptRequest {

    @NotNull(message = "채택할 댓글 ID는 필수입니다.")
    private Long commentId;
}

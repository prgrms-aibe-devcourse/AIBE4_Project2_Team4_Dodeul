// src/main/java/org/aibe4/dodeul/domain/board/model/dto/request/BoardPostUpdateRequest.java
package org.aibe4.dodeul.domain.board.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;

@Getter
@NoArgsConstructor
public class BoardPostUpdateRequest {

    @NotBlank(message = "제목은 공백일 수 없습니다.")
    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
    private String title;

    @NotBlank(message = "내용은 공백일 수 없습니다.")
    private String content;

    @NotNull(message = "상담분야는 필수입니다.")
    private ConsultingTag consultingTag;
}

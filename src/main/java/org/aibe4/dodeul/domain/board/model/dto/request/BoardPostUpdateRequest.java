// src/main/java/org/aibe4/dodeul/domain/board/model/dto/request/BoardPostUpdateRequest.java
package org.aibe4.dodeul.domain.board.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "게시글 수정 요청")
public class BoardPostUpdateRequest {

    @NotNull(message = "상담분야는 필수입니다.")
    @Schema(description = "상담 분야", example = "RESUME")
    private ConsultingTag consultingTag;

    @NotBlank(message = "제목은 공백일 수 없습니다.")
    @Size(min = 2, max = 100, message = "제목은 2자 이상 100자 이하여야 합니다.")
    @Schema(description = "게시글 제목", minLength = 2, maxLength = 100, example = "수정된 제목입니다.")
    private String title;

    @NotBlank(message = "내용은 공백일 수 없습니다.")
    @Size(min = 10, message = "내용은 10자 이상이어야 합니다.")
    @Schema(description = "게시글 본문", minLength = 10, example = "수정된 본문 내용입니다...")
    private String content;

    @Schema(description = "선택된 기존 스킬 태그 ID 목록", example = "[2, 3]")
    private List<Long> skillTagIds = new ArrayList<>();

    @Schema(description = "직접 입력한 신규 스킬 태그 이름 목록", example = "[\"UpdatedTag\"]")
    private List<String> skillTagNames = new ArrayList<>();
}

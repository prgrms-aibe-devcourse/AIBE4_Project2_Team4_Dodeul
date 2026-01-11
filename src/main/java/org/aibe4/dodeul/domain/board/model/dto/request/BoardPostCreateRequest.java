// src/main/java/org/aibe4/dodeul/domain/board/model/dto/request/BoardPostCreateRequest.java
package org.aibe4.dodeul.domain.board.model.dto.request;

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
public class BoardPostCreateRequest {

    @NotNull(message = "상담분야는 필수입니다.")
    private ConsultingTag consultingTag;

    @NotBlank(message = "제목은 공백일 수 없습니다.")
    @Size(min = 2, max = 100, message = "제목은 2자 이상 100자 이하여야 합니다.")
    private String title;

    @NotBlank(message = "내용은 공백일 수 없습니다.")
    @Size(min = 10, message = "내용은 10자 이상이어야 합니다.")
    private String content;

    // 체크박스(기존 태그) 선택 결과
    private List<Long> skillTagIds = new ArrayList<>();

    // 입력창(쉼표 구분)에서 받은 신규/이름 태그
    private List<String> skillTagNames = new ArrayList<>();

    // Thymeleaf 폼 바인딩용 Setter
    public void setConsultingTag(ConsultingTag consultingTag) {
        this.consultingTag = consultingTag;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // ViewController에서 수동 파싱 결과 주입용
    public void setSkillTagIds(List<Long> skillTagIds) {
        this.skillTagIds =
            (skillTagIds == null) ? new ArrayList<>() : new ArrayList<>(skillTagIds);
    }

    public void setSkillTagNames(List<String> skillTagNames) {
        this.skillTagNames =
            (skillTagNames == null) ? new ArrayList<>() : new ArrayList<>(skillTagNames);
    }
}

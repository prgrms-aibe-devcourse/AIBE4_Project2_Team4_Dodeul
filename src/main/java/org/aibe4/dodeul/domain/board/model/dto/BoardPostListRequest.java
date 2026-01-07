// src/main/java/org/aibe4/dodeul/domain/board/model/dto/BoardPostListRequest.java
package org.aibe4.dodeul.domain.board.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardPostListRequest {

    private ConsultingTag consultingTag;
    private List<Long> skillTagIds;

    // 없거나 잘못되면 OPEN
    private String status;

    private String keyword;

    // LATEST / VIEWS / ACTIVE
    private String sort;
}

package org.aibe4.dodeul.domain.search.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.aibe4.dodeul.domain.search.model.enums.SortType;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MentorSearchCondition {
    private String keyword;
    private List<String> jobs;
    private List<String> skillTags;
    private List<ConsultingTag> consultingTags;
    private Boolean onlyAvailable;
    private SortType sortType;
}

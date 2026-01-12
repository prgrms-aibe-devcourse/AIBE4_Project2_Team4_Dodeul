package org.aibe4.dodeul.domain.consultation.model.dto;

import lombok.Builder;
import lombok.Getter;
import org.aibe4.dodeul.domain.consultation.model.entity.ConsultationRoom;
import org.aibe4.dodeul.domain.consulting.model.dto.ConsultingApplicationDetailResponse;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;
import org.aibe4.dodeul.domain.matching.model.entity.Matching;

import java.util.List;
import java.util.stream.Stream;

@Getter
@Builder
public class ConsultationRoomDto {

    private Long consultationRoomId;
    private Long currentMemberId;

    private List<MemberDto> participants;
    private ConsultingApplicationDetailResponse consultingApplicationDetailResponse;
    private List<MessageDto> messageDtoList;

    public static ConsultationRoomDto of(ConsultationRoom room, List<MessageDto> messageDtoList, Long currentMemberId) {

        Matching matching = room.getValidatedMatching();
        ConsultingApplication application = room.getValidatedApplication();
        ConsultingApplicationDetailResponse consultingApplicationDetailResponse = ConsultingApplicationDetailResponse.from(application);
        List<MemberDto> participants = Stream.of(matching.getMentor(), matching.getMentee())
            .map(MemberDto::of)
            .toList();

        return ConsultationRoomDto.builder()
            .consultationRoomId(room.getId())
            .currentMemberId(currentMemberId)
            .participants(participants)
            .consultingApplicationDetailResponse(consultingApplicationDetailResponse)
            .messageDtoList(messageDtoList)
            .build();
    }
}

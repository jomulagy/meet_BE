package com.example.meet.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindUserScheduleVoteResponseDto {
    private List<UserScheduleVoteItemResponseDto> userScheduleVoteItemList;
}

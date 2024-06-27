package com.example.meet.common.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateScheduleVoteRequestDto {
    private Long userId;
    private Long meetId;
    private List<Long> scheduleVoteItemList;
}

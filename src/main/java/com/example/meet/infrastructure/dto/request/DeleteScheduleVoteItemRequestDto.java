package com.example.meet.infrastructure.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteScheduleVoteItemRequestDto {
    private Long userId;
    private Long scheduleVoteItemId;
}

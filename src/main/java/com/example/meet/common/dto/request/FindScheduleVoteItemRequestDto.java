package com.example.meet.common.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindScheduleVoteItemRequestDto {
    private Long meetId;
    private Long userId;
}
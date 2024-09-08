package com.example.meet.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindScheduleVoteResponseDto {
    private String meetTitle;
    private String endDate;
    private String isAuthor;
}

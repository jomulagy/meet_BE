package com.example.meet.infrastructure.dto.response.participate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindParticipateVoteResponseDto {
    private String meetTitle;
    private String date;
    private String time;
    private String place;
    private String endDate;
    private String isAuthor;
}

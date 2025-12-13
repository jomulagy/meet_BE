package com.example.meet.vote.application.adapter.in.dto.in;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateScheduleVoteItemRequestDto {
    private Long meetId;
    private LocalDate date;
    private LocalTime time;
    private Long userId;
}

package com.example.meet.common.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateScheduleVoteItemRequestDto {
    @NotNull
    private Long meetId;
    @NotNull
    private LocalDate date;
    private Long userId;
}

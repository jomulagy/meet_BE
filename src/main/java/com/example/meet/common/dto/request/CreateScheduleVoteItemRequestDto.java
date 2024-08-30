package com.example.meet.common.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateScheduleVoteItemRequestDto {
    @NotNull
    private Long meetId;
    @NotNull
    private LocalDate date;
    @NotNull
    private LocalTime time;
    @Schema(hidden = true)
    private Long userId;
}

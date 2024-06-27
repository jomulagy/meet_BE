package com.example.meet.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateScheduleVoteItemResponseDto {
    @Schema(description = "날짜 투표 항목 id")
    private String scheduleVoteItemId;
}

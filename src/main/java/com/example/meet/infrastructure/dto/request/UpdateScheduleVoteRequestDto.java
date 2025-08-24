package com.example.meet.infrastructure.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(hidden = true)
    private Long userId;
    private Long meetId;
    @Schema(description = "일정 투표 항목 id", example = "[1,2,3]")
    private List<Long> scheduleVoteItemList;
}

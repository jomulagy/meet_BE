package com.example.meet.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindMeetRequestDto {
    @Schema(hidden = true)
    private Long userId;
    @Schema(description = "모임 id", example = "1")
    private Long meetId;
}

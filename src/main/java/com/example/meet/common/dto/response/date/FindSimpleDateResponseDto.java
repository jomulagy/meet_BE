package com.example.meet.common.dto.response.date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindSimpleDateResponseDto {
    @Schema(description = "모임 날짜", example = "2024-07-05")
    private String value;

    @Schema(description = "모임 시간", example = "19:00")
    private String time;

    @Schema(description = "수정 가능 여부", example = "true")
    private String editable;
}

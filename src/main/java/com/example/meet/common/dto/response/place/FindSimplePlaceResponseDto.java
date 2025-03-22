package com.example.meet.common.dto.response.place;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindSimplePlaceResponseDto {
    @Schema(description = "모임 장소", example = "강남역")
    private String value;

    @Schema(description = "수정 가능 여부", example = "true")
    private String editable;
}

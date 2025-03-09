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
    private String name;

    @Schema(description = "x 좌표", example = "37.49809895356626")
    private String xPos;

    @Schema(description = "y 좌표", example = "127.02798897144342")
    private String yPos;

    @Schema(description = "장소", example = "강남역 삼성 스토어")
    private String detail;

    @Schema(description = "수정 가능 여부", example = "true")
    private String editable;
}

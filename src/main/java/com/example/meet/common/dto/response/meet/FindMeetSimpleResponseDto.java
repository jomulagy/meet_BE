package com.example.meet.common.dto.response.meet;

import com.example.meet.common.dto.response.place.FindSimplePlaceResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindMeetSimpleResponseDto {
    @Schema(description = "모임 id", example = "1")
    private Long id;
    @Schema(description = "모임 제목", example = "2024 3분기 정기모임")
    private String title;
    @Schema(description = "모임 날짜")
    private String date;
    @Schema(description = "모임 장소")
    private FindSimplePlaceResponseDto place;
}

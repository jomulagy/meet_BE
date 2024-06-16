package com.example.meet.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMeetResponseDto {
    @Schema(description = "모임 id", example = "1")
    private Long id;
}

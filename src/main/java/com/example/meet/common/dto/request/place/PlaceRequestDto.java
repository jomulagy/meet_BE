package com.example.meet.common.dto.request.place;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Schema(description = "모임 장소 정보", implementation = PlaceRequestDto.class)
public class PlaceRequestDto {
    @Schema(description = "장소 명", example = "강남역")
    private String name;

    @JsonProperty("xPos")
    @Schema(description = "징소 x 좌표", example = "12.11")
    private BigDecimal xPos;

    @JsonProperty("yPos")
    @Schema(description = "장소 y 좌표", example = "123.45")
    private BigDecimal yPos;

    @JsonProperty("detail")
    @Schema(description = "장소", example = "강남역 삼성 스토어")
    private String detail;
}

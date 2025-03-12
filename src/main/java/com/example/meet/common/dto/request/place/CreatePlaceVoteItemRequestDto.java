package com.example.meet.common.dto.request.place;

import com.example.meet.common.dto.response.place.FindSimplePlaceResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePlaceVoteItemRequestDto {
    @NotNull
    @Schema(description = "모임 id", example = "1")
    private Long meetId;

    @NotNull
    @Schema(description = "장소", example = "강남역")
    private PlaceRequestDto place;

    @Schema(hidden = true)
    private Long userId;
}

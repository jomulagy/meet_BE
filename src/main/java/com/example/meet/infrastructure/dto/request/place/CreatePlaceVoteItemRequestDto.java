package com.example.meet.infrastructure.dto.request.place;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
    private String place;

    @Schema(hidden = true)
    private Long userId;
}

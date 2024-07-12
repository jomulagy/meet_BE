package com.example.meet.common.dto.request.place;

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
    private Long meetId;
    @NotNull
    private String place;
    @Schema(hidden = true)
    private Long userId;
}

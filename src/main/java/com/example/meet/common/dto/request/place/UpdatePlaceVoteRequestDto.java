package com.example.meet.common.dto.request.place;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePlaceVoteRequestDto {
    @Schema(hidden = true)
    private Long userId;
    private Long meetId;
    @Schema(description = "장소 투표 항목 id", example = "[1,2,3]")
    private List<Long> placeVoteItemList;
}

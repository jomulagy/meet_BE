package com.example.meet.participate.adapter.in.dto.in;

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
public class UpdateParticipateVoteRequestDto {
    @Schema(hidden = true)
    private Long userId;
    private Long meetId;
    @Schema(description = "참여여부 투표 항목 id 리스트", example = "[1,2,3]")
    private List<Long> participateVoteItemIdList;
}

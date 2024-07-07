package com.example.meet.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateScheduleVoteItemResponseDto {
    @Schema(description = "날짜 투표 항목 id")
    private String id;
    @Schema(description = "날짜")
    private String date;
    @Schema(description = "수정 가능 여부")
    private String editable;
    @Schema(description = "투표 여부")
    private String isVote;
    @Schema(description = "투표한 멤버 리스트")
    private List<SimpleMemberResponseDto> memberList;
}
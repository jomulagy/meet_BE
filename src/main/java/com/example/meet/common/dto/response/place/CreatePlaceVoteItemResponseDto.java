package com.example.meet.common.dto.response.place;

import com.example.meet.common.dto.response.member.SimpleMemberResponseDto;
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
public class CreatePlaceVoteItemResponseDto {
    @Schema(description = "장소 투표 항목 id")
    private String id;
    @Schema(description = "장소")
    private String place;
    @Schema(description = "수정 가능 여부")
    private String editable;
    @Schema(description = "투표 여부")
    private String isVote;
    @Schema(description = "투표한 멤버 리스트")
    private List<SimpleMemberResponseDto> memberList;
}

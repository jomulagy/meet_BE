package com.example.meet.common.dto.response.participate;

import com.example.meet.common.dto.response.SimpleMemberResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class FindParticipateVoteItemResponseDto {
    private String id;
    @Schema(description = "투표 항목 이름")
    private String name;
    @Schema(description = "투표 여부")
    private String isVote;
    @Schema(description = "투표자 목록")
    private List<SimpleMemberResponseDto> memberList;
}

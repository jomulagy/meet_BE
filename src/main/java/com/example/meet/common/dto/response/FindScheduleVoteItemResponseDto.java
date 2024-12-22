package com.example.meet.common.dto.response;

import com.example.meet.common.dto.response.member.SimpleMemberResponseDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class FindScheduleVoteItemResponseDto {
    private String id;
    private String date;
    private String time;
    private String editable;
    private String isVote;
    private List<SimpleMemberResponseDto> memberList;
}

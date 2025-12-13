package com.example.meet.vote.adapter.in.dto.out;

import com.example.meet.infrastructure.dto.response.member.SimpleMemberResponseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetVoteItemResponseDto {
    private String id;
    private String date;
    private String time;
    private String editable;
    private String isVote;
    private List<SimpleMemberResponseDto> memberList;
}

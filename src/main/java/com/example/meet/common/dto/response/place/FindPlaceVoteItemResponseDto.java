package com.example.meet.common.dto.response.place;

import com.example.meet.common.dto.response.SimpleMemberResponseDto;
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
public class FindPlaceVoteItemResponseDto {
    private String id;
    private String place;
    private String editable;
    private String isVote;
    private List<SimpleMemberResponseDto> memberList;
}

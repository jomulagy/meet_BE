package com.example.meet.vote.adapter.in.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindVoteResponseDto {
    private Long id;
    private String title;
    private String endDate;
    private boolean isDuplicate;
    private boolean isActive;
    private boolean isVoted;
    private String type;
    private String result;
    private List<FindVoteItemResponseDto> itemList;
}

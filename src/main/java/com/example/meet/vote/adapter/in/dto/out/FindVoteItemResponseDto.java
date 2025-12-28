package com.example.meet.vote.adapter.in.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindVoteItemResponseDto {
    private Long id;
    private String value;
    private boolean isVoted;
    private boolean editable;
    private List<String> voterList;
}

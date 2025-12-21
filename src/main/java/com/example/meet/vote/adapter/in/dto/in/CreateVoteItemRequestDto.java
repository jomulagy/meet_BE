package com.example.meet.vote.adapter.in.dto.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVoteItemRequestDto {
    private Long voteId;
    private String date;
    private String time;
}

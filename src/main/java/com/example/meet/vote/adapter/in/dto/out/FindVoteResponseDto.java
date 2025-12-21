package com.example.meet.vote.adapter.in.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindVoteResponseDto {
    private String title;
    private String result;
    private String endDate;
}

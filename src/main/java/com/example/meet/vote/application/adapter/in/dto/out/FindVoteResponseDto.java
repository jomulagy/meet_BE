package com.example.meet.vote.application.adapter.in.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindVoteResponseDto {
    private String meetTitle;
    private String endDate;
    private String isAuthor;
}

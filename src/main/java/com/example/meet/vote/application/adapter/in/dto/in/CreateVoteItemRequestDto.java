package com.example.meet.vote.application.adapter.in.dto.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVoteItemRequestDto {
    private Long userId;
    private Long meetId;
    private String content;
}

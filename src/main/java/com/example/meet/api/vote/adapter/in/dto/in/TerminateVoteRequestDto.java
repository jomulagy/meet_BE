package com.example.meet.api.vote.adapter.in.dto.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TerminateVoteRequestDto {
    private Long voteId;
    private Long postId;
}

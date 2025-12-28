package com.example.meet.vote.adapter.in.dto.in;

import com.example.meet.infrastructure.enumulation.VoteType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateVoteRequestDto {
    private Long postId;
    private String duplicateYn;
    private String title;
    private String voteDeadline;
    private VoteType voteType;
}

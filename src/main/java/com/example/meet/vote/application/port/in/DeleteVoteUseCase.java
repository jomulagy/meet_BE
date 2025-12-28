package com.example.meet.vote.application.port.in;

import com.example.meet.vote.adapter.in.dto.out.DeleteVoteResponseDto;

public interface DeleteVoteUseCase {
    DeleteVoteResponseDto deleteVote(Long voteId);
}

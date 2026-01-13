package com.example.meet.api.vote.application.port.in;

import com.example.meet.api.vote.adapter.in.dto.out.DeleteVoteResponseDto;

public interface DeleteVoteUseCase {
    DeleteVoteResponseDto deleteVote(Long voteId);
}

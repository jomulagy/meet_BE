package com.example.meet.participate.application.port.in;

import com.example.meet.participate.adapter.in.dto.in.TerminateParticipateVoteRequestDto;
import com.example.meet.participate.adapter.in.dto.in.VoteParticipateVoteRequestDto;

public interface UpdateParticipateVoteUseCase {
    void terminate(TerminateParticipateVoteRequestDto inDto);

    void vote(VoteParticipateVoteRequestDto inDto);
}

package com.example.meet.api.vote.application.port.in;

import com.example.meet.api.vote.adapter.in.dto.in.TerminateVoteRequestDto;
import com.example.meet.api.vote.adapter.in.dto.in.UpdateVoteRequestDto;
import com.example.meet.api.vote.adapter.in.dto.out.FindVoteResponseDto;
import com.example.meet.api.vote.adapter.in.dto.out.TerminateResponseDto;

public interface UpdateVoteUseCase {
    FindVoteResponseDto vote(UpdateVoteRequestDto inDto);

    TerminateResponseDto terminate(TerminateVoteRequestDto request);

    void terminateAll(TerminateVoteRequestDto request);
}

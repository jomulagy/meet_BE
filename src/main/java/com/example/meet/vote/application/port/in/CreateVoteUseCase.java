package com.example.meet.vote.application.port.in;

import com.example.meet.vote.adapter.in.dto.in.CreateVoteRequestDto;

public interface CreateVoteUseCase {
    void create(CreateVoteRequestDto request);
}

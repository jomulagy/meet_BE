package com.example.meet.api.vote.application.port.in;

import com.example.meet.api.vote.adapter.in.dto.in.CreateVoteRequestDto;

public interface CreateVoteUseCase {
    void create(CreateVoteRequestDto request);
}

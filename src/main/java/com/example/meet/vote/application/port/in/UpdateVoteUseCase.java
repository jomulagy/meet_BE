package com.example.meet.vote.application.port.in;

import com.example.meet.vote.adapter.in.dto.in.UpdateVoteRequestDto;
import com.example.meet.vote.adapter.in.dto.out.UpdateVoteResponseDto;

public interface UpdateVoteUseCase {
    UpdateVoteResponseDto vote(UpdateVoteRequestDto inDto);
}

package com.example.meet.participate.application.port.in;

import com.example.meet.participate.adapter.in.dto.in.FindParticipateVoteRequestDto;
import com.example.meet.infrastructure.dto.response.participate.FindParticipateVoteResponseDto;

public interface GetParticipateUseCase {
    FindParticipateVoteResponseDto get(FindParticipateVoteRequestDto inDto);
}

package com.example.meet.api.participate.application.port.in;

import com.example.meet.api.participate.adapter.in.dto.in.FindParticipateVoteRequestDto;
import com.example.meet.infrastructure.dto.response.participate.FindParticipateVoteResponseDto;

public interface GetParticipateUseCase {
    FindParticipateVoteResponseDto get(FindParticipateVoteRequestDto inDto);
}

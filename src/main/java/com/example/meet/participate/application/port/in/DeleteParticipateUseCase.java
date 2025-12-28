package com.example.meet.participate.application.port.in;

import com.example.meet.infrastructure.dto.response.participate.UpdateParticipateVoteResponseDto;

public interface DeleteParticipateUseCase {
    void delete(Long postId);
}


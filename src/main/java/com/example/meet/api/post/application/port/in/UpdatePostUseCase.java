package com.example.meet.api.post.application.port.in;

import com.example.meet.api.post.adapter.in.dto.in.UpdatePostRequestDto;
import com.example.meet.api.post.adapter.in.dto.out.UpdatePostResponseDto;

public interface UpdatePostUseCase {
    UpdatePostResponseDto update(UpdatePostRequestDto inDto);

    void terminateVote(Long id);
}

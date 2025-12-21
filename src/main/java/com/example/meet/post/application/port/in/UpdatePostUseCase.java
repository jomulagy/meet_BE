package com.example.meet.post.application.port.in;

import com.example.meet.post.adapter.in.dto.in.UpdatePostRequestDto;
import com.example.meet.post.adapter.in.dto.out.UpdatePostResponseDto;

public interface UpdatePostUseCase {
    UpdatePostResponseDto update(UpdatePostRequestDto inDto);
}

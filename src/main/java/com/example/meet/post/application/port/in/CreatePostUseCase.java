package com.example.meet.post.application.port.in;

import com.example.meet.post.adapter.in.dto.CreateMeetRequestDto;
import com.example.meet.post.adapter.in.dto.CreateMeetResponseDto;

public interface CreatePostUseCase {
    CreateMeetResponseDto createMeet(CreateMeetRequestDto requestDto);
}

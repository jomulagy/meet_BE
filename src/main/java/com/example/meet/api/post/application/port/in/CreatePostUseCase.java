package com.example.meet.api.post.application.port.in;

import com.example.meet.api.post.adapter.in.dto.CreateMeetRequestDto;
import com.example.meet.api.post.adapter.in.dto.CreateMeetResponseDto;

public interface CreatePostUseCase {
    CreateMeetResponseDto createMeet(CreateMeetRequestDto requestDto);

    CreateMeetResponseDto createNotification(CreateMeetRequestDto requestDto);

    CreateMeetResponseDto createVote(CreateMeetRequestDto requestDto);

    CreateMeetResponseDto createTravel(CreateMeetRequestDto requestDto);
}

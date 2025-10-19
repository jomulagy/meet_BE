package com.example.meet.meet.application.port.in;

import com.example.meet.meet.adapter.in.dto.CreateMeetRequestDto;
import com.example.meet.meet.adapter.in.dto.CreateMeetResponseDto;

public interface CreateMeetUseCase {
    CreateMeetResponseDto create(CreateMeetRequestDto requestDto);
}

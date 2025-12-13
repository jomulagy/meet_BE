package com.example.meet.place.application.port.in;

import com.example.meet.place.adapter.in.dto.request.FindPlaceVoteRequestDto;
import com.example.meet.place.adapter.in.dto.response.FindPlaceVoteResponseDto;

public interface GetPlaceVoteUseCase {
    FindPlaceVoteResponseDto findPlaceVote(FindPlaceVoteRequestDto inDto);
}

package com.example.meet.place.application.port.in;

import com.example.meet.place.adapter.out.dto.request.FindPlaceVoteRequestDto;
import com.example.meet.place.adapter.out.dto.response.FindPlaceVoteResponseDto;

public interface GetPlaceVoteUseCase {
    FindPlaceVoteResponseDto findPlaceVote(FindPlaceVoteRequestDto inDto);
}

package com.example.meet.api.vote.application.port.in;

import com.example.meet.api.vote.adapter.in.dto.in.CreateVoteItemRequestDto;
import com.example.meet.api.vote.adapter.in.dto.out.FindVoteResponseDto;

public interface CreateVoteItemUseCase {
    FindVoteResponseDto createItem(CreateVoteItemRequestDto inDto);
}

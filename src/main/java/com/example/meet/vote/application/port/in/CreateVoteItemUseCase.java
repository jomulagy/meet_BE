package com.example.meet.vote.application.port.in;

import com.example.meet.vote.adapter.in.dto.in.CreateVoteItemRequestDto;
import com.example.meet.vote.adapter.in.dto.out.CreateVoteItemResponseDto;

public interface CreateVoteItemUseCase {
    CreateVoteItemResponseDto createItem(CreateVoteItemRequestDto inDto);
}

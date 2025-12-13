package com.example.meet.vote.application.port.in;

import com.example.meet.vote.adapter.in.dto.in.FindVoteItemRequestDto;
import com.example.meet.vote.adapter.in.dto.out.FindVoteItemResponseDto;

import java.util.List;

public interface GetVoteItemUseCase {
    List<FindVoteItemResponseDto> getItemList(FindVoteItemRequestDto inDto);
}

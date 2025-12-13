package com.example.meet.vote.application.port.in;

import com.example.meet.vote.adapter.in.dto.in.FindVoteItemRequestDto;
import com.example.meet.vote.adapter.in.dto.out.GetVoteItemResponseDto;

import java.util.List;

public interface GetVoteItemUseCase {
    List<GetVoteItemResponseDto> getItemList(FindVoteItemRequestDto inDto);
}

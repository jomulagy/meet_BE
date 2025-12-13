package com.example.meet.vote.application.port.in;

import com.example.meet.vote.adapter.in.dto.in.CreateVoteItemRequestDto;
import com.example.meet.vote.adapter.in.dto.in.DeleteVoteItemRequestDto;
import com.example.meet.vote.adapter.in.dto.in.FindVoteItemRequestDto;
import com.example.meet.vote.adapter.in.dto.out.CreateVoteItemResponseDto;
import com.example.meet.vote.adapter.in.dto.out.DeleteVoteItemResponseDto;
import com.example.meet.vote.adapter.in.dto.out.FindVoteItemResponseDto;
import java.util.List;

public interface VoteItemUseCase {
    List<FindVoteItemResponseDto> getItemList(FindVoteItemRequestDto inDto);

    CreateVoteItemResponseDto createItem(CreateVoteItemRequestDto inDto);

    DeleteVoteItemResponseDto deleteItem(DeleteVoteItemRequestDto inDto);
}

package com.example.meet.vote.application.port.in;

import com.example.meet.vote.adapter.in.dto.in.DeleteVoteItemRequestDto;
import com.example.meet.vote.adapter.in.dto.out.DeleteVoteItemResponseDto;

public interface DeleteVoteItemUseCase {
    DeleteVoteItemResponseDto deleteItem(DeleteVoteItemRequestDto inDto);
}

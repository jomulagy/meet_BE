package com.example.meet.api.vote.application.port.in;

import com.example.meet.api.vote.adapter.in.dto.in.DeleteVoteItemRequestDto;
import com.example.meet.api.vote.adapter.in.dto.out.DeleteVoteItemResponseDto;

public interface DeleteVoteItemUseCase {
    DeleteVoteItemResponseDto deleteItem(DeleteVoteItemRequestDto inDto);
}

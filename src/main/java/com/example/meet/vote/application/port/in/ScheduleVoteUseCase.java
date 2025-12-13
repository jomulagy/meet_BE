package com.example.meet.vote.application.port.in;

import com.example.meet.vote.adapter.in.dto.in.*;
import com.example.meet.vote.adapter.in.dto.out.*;

import java.util.List;

public interface ScheduleVoteUseCase {
    FindVoteResponseDto get(FindVoteRequestDto inDto);

    List<FindVoteItemResponseDto> getItemList(FindVoteItemRequestDto inDto);

    CreateVoteItemResponseDto createItem(CreateVoteItemRequestDto inDto);

    DeleteVoteItemResponseDto deleteItem(DeleteVoteItemRequestDto inDto);

    UpdateVoteResponseDto update(UpdateVoteRequestDto inDto);
}

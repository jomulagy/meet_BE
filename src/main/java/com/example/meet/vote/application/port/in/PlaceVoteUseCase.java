package com.example.meet.vote.application.port.in;

import com.example.meet.infrastructure.dto.request.place.CreatePlaceVoteItemRequestDto;
import com.example.meet.infrastructure.dto.request.place.DeletePlaceVoteItemRequestDto;
import com.example.meet.infrastructure.dto.request.place.FindPlaceVoteItemRequestDto;
import com.example.meet.infrastructure.dto.request.place.UpdatePlaceVoteRequestDto;
import com.example.meet.infrastructure.dto.response.place.CreatePlaceVoteItemResponseDto;
import com.example.meet.infrastructure.dto.response.place.DeletePlaceVoteItemResponseDto;
import com.example.meet.infrastructure.dto.response.place.FindPlaceVoteItemResponseDto;
import com.example.meet.infrastructure.dto.response.place.UpdatePlaceVoteResponseDto;
import java.util.List;

public interface PlaceVoteUseCase {
    List<FindPlaceVoteItemResponseDto> findPlaceVoteItemList(FindPlaceVoteItemRequestDto inDto);

    CreatePlaceVoteItemResponseDto createPlaceVoteItem(CreatePlaceVoteItemRequestDto inDto);

    DeletePlaceVoteItemResponseDto deletePlaceVoteItem(DeletePlaceVoteItemRequestDto inDto);

    UpdatePlaceVoteResponseDto updatePlaceVote(UpdatePlaceVoteRequestDto inDto);
}

package com.example.meet.mapper;

import com.example.meet.common.dto.request.place.CreatePlaceVoteItemRequestDto;
import com.example.meet.entity.PlaceVote;
import com.example.meet.entity.PlaceVoteItem;
import org.mapstruct.factory.Mappers;

public interface PlaceVoteItemMapper {

    PlaceVoteItemMapper INSTANCE = Mappers.getMapper(PlaceVoteItemMapper.class);

    default PlaceVoteItem toEntity(CreatePlaceVoteItemRequestDto inDto, PlaceVote placeVote){
        return PlaceVoteItem.builder()
                .place(inDto.getPlace())
                .editable(true)
                .placeVote(placeVote)
                .build();
    }
}

package com.example.meet.infrastructure.mapper;

import com.example.meet.infrastructure.dto.request.place.CreatePlaceVoteItemRequestDto;
import com.example.meet.entity.Member;
import com.example.meet.entity.PlaceVote;
import com.example.meet.entity.PlaceVoteItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PlaceVoteItemMapper {

    PlaceVoteItemMapper INSTANCE = Mappers.getMapper(PlaceVoteItemMapper.class);

    default PlaceVoteItem toEntity(CreatePlaceVoteItemRequestDto inDto, PlaceVote placeVote, Member user){
        return PlaceVoteItem.builder()
                .place(inDto.getPlace())
                .editable(true)
                .author(user)
                .placeVote(placeVote)
                .build();
    }
}

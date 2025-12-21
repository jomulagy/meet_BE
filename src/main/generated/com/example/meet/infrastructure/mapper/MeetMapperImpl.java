package com.example.meet.infrastructure.mapper;

import com.example.meet.post.adapter.in.dto.CreateMeetResponseDto;
import com.example.meet.post.application.domain.entity.Post;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-21T23:19:09+0900",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 17.0.10 (Oracle Corporation)"
)
public class MeetMapperImpl implements MeetMapper {

    @Override
    public CreateMeetResponseDto entityToCreateDto(Post entity) {
        if ( entity == null ) {
            return null;
        }

        CreateMeetResponseDto.CreateMeetResponseDtoBuilder createMeetResponseDto = CreateMeetResponseDto.builder();

        createMeetResponseDto.id( entity.getId() );

        return createMeetResponseDto.build();
    }
}

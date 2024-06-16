package com.example.meet.mapper;

import com.example.meet.common.dto.request.CreateMeetRequestDto;
import com.example.meet.common.dto.response.CreateMeetResponseDto;
import com.example.meet.entity.Meet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeetMapper {
    MeetMapper INSTANCE = Mappers.getMapper(MeetMapper.class);
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    default Meet dtoToEntity(CreateMeetRequestDto dto){
        LocalDateTime date = LocalDateTime.parse(dto.getDate()+" 19:00", DATE_TIME_FORMATTER);
        return Meet.builder()
                .title(dto.getTitle())
                .type(dto.getType())
                .date(date)
                .place(dto.getPlace())
                .content(dto.getContent())
                .build();
    }

    @Mapping(source = "id", target = "id")
    CreateMeetResponseDto entityToCreateDto(Meet entity);
}

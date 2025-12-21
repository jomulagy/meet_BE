package com.example.meet.infrastructure.mapper;

import com.example.meet.post.adapter.in.dto.CreateMeetRequestDto;
import com.example.meet.post.adapter.in.dto.CreateMeetResponseDto;
import com.example.meet.post.application.domain.entity.Post;
import com.example.meet.entity.Member;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeetMapper {
    MeetMapper INSTANCE = Mappers.getMapper(MeetMapper.class);


    @Mapping(source = "id", target = "id")
    CreateMeetResponseDto entityToCreateDto(Post entity);
}

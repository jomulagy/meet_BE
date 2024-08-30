package com.example.meet.mapper;

import com.example.meet.common.dto.request.CreateScheduleVoteItemRequestDto;
import com.example.meet.entity.Member;
import com.example.meet.entity.ScheduleVote;
import com.example.meet.entity.ScheduleVoteItem;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ScheduleVoteItemMapper {
    ScheduleVoteItemMapper INSTANCE = Mappers.getMapper(ScheduleVoteItemMapper.class);


    default ScheduleVoteItem toEntity(CreateScheduleVoteItemRequestDto inDto, ScheduleVote scheduleVote, Member user){
        return ScheduleVoteItem.builder()
                .date(LocalDateTime.of(inDto.getDate(), inDto.getTime()))
                .editable(true)
                .author(user)
                .scheduleVote(scheduleVote)
                .build();
    }
}

package com.example.meet.infrastructure.mapper;

import com.example.meet.infrastructure.dto.request.CreateMeetRequestDto;
import com.example.meet.infrastructure.dto.response.meet.CreateMeetResponseDto;
import com.example.meet.infrastructure.dto.response.meet.EditMeetResponseDto;
import com.example.meet.infrastructure.dto.response.meet.FindMeetResponseDto;
import com.example.meet.infrastructure.dto.response.date.FindSimpleDateResponseDto;
import com.example.meet.infrastructure.dto.response.meet.FindMeetSimpleResponseDto;
import com.example.meet.infrastructure.dto.response.place.FindSimplePlaceResponseDto;
import com.example.meet.meet.domain.entity.Meet;
import com.example.meet.entity.Member;
import com.example.meet.entity.Place;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Context;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeetMapper {
    MeetMapper INSTANCE = Mappers.getMapper(MeetMapper.class);
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    default Meet dtoToEntity(CreateMeetRequestDto dto, Member author){
        LocalDate date = null;
        LocalTime time = null;
        LocalDate currentDate = LocalDate.now();
        LocalDate tomorrowDate = currentDate.plusDays(2);
        LocalDateTime endDate = tomorrowDate.atTime(LocalTime.of(0, 0));
        Place place;

        if(dto.getDate() != null){
            date = LocalDate.parse(dto.getDate(), DATE_FORMATTER);
        }
        if(dto.getTime() != null){
            time = LocalTime.parse(dto.getTime(), TIME_FORMATTER);
        }

        LocalDateTime dateTime = null;
        if (date != null && time != null) {
            dateTime = date.atTime(time);
        }

        return Meet.builder()
                .title(dto.getTitle())
                .type(dto.getType())
                .date(dateTime)
                .place(dto.getPlace())
                .content(dto.getContent())
                .author(author)
                .endDate(endDate)
                .build();
    }

    @Mapping(source = "id", target = "id")
    CreateMeetResponseDto entityToCreateDto(Meet entity);


    default FindMeetResponseDto EntityToDto(Meet entity, @Context Member user){
        FindSimpleDateResponseDto dateResponseDto;
        FindSimplePlaceResponseDto placeResponseDto = null;

        String date = null;
        String time = null;

        if(entity.getDate() != null){
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            date = entity.getDate().toLocalDate().format(dateFormatter);
            time = entity.getDate().toLocalTime().format(timeFormatter);
        }

        Boolean editable = false;

        if(entity.getAuthor().equals(user) && entity.getScheduleVote() == null){
            editable = true;
        }

        dateResponseDto = FindSimpleDateResponseDto.builder()
                .value(date)
                .time(time)
                .editable(editable.toString())
                .build();

        if(entity.getAuthor().equals(user) && entity.getPlaceVote() == null){
            editable = true;
        }

        placeResponseDto = FindSimplePlaceResponseDto.builder()
                .value(entity.getPlace())
                .editable(editable.toString())
                .build();

        Long participantsNum = entity.getParticipantsNum();
        if(entity.getParticipantsNum() == null){
            participantsNum = 0L;
        }

        List<String> participants = new ArrayList<>();

        for(Member m : entity.getParticipants()){
            participants.add(m.getName());
        }
        return FindMeetResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .type(entity.getType())
                .date(dateResponseDto)
                .place(placeResponseDto)
                .participantsNum(String.valueOf(participantsNum))
                .participants(participants)
                .isAuthor(String.valueOf(entity.getAuthor().equals(user)))
                .build();
    }

    @Named("EntityToDtoSimple")
    default FindMeetSimpleResponseDto EntityToDtoSimple(Meet entity){
        String date = null;
        String place = null;

        if(entity.getDate() != null){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            date = entity.getDate().format(dateTimeFormatter);
        }

        return FindMeetSimpleResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .date(date)
                .place(entity.getPlace())
                .build();
    }

    default EditMeetResponseDto EntityToUpdateDto(Meet entity){
        String date = null;

        if(entity.getDate() != null){
            date = entity.getDate().format(DATE_TIME_FORMATTER);
        }
        Long participantsNum = entity.getParticipantsNum();
        if(entity.getParticipantsNum() == null){
            participantsNum = 0L;
        }

        List<String> participants = new ArrayList<>();

        for(Member m : entity.getParticipants()){
            participants.add(m.getName());
        }

        return EditMeetResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .type(entity.getType())
                .date(date)
                .place(entity.getPlace())
                .participantsNum(String.valueOf(participantsNum))
                .participants(participants)
                .build();
    }

    @IterableMapping(qualifiedByName = "EntityToDtoSimple")
    ArrayList<FindMeetSimpleResponseDto> EntityListToDtoList(List<Meet> meetList);
}

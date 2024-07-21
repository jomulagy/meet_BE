package com.example.meet.mapper;

import com.example.meet.common.dto.request.CreateMeetRequestDto;
import com.example.meet.common.dto.response.CreateMeetResponseDto;
import com.example.meet.common.dto.response.EditMeetResponseDto;
import com.example.meet.common.dto.response.FindMeetResponseDto;
import com.example.meet.common.dto.response.date.FindSimpleDateResponseDto;
import com.example.meet.common.dto.response.place.FindSimplePLaceResponseDto;
import com.example.meet.common.enumulation.ErrorCode;
import com.example.meet.common.exception.BusinessException;
import com.example.meet.entity.Meet;
import com.example.meet.entity.Member;
import com.example.meet.entity.PlaceVote;
import com.example.meet.entity.PlaceVoteItem;
import com.example.meet.entity.ScheduleVote;
import com.example.meet.entity.ScheduleVoteItem;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeetMapper {
    MeetMapper INSTANCE = Mappers.getMapper(MeetMapper.class);
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    default Meet dtoToEntity(CreateMeetRequestDto dto, Member author){
        LocalDateTime date = null;

        if(dto.getDate() != null){
            date = LocalDateTime.parse(dto.getDate()+" 19:00", DATE_TIME_FORMATTER);
        }

        return Meet.builder()
                .title(dto.getTitle())
                .type(dto.getType())
                .date(date)
                .place(dto.getPlace())
                .content(dto.getContent())
                .author(author)
                .build();
    }

    @Mapping(source = "id", target = "id")
    CreateMeetResponseDto entityToCreateDto(Meet entity);

    default FindMeetResponseDto EntityToDto(Meet entity, Member user){
        FindSimpleDateResponseDto date = null;
        FindSimplePLaceResponseDto place = null;

        if(entity.getDate() != null){
            ScheduleVote scheduleVote = entity.getScheduleVote();
            ScheduleVoteItem scheduleVoteItem = scheduleVote.getScheduleVoteItems().stream()
                    .filter(item -> item.getDate().equals(scheduleVote.getDateResult()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));

            Boolean editable = false;

            if(scheduleVoteItem.getEditable() && entity.getAuthor() == user){
                editable = true;
            }
            date = FindSimpleDateResponseDto.builder()
                    .value(scheduleVoteItem.getDate().toString())
                    .editable(editable.toString())
                    .build();
        }

        if(entity.getPlace() != null){
            PlaceVote placeVote = entity.getPlaceVote();
            PlaceVoteItem placeVoteItem = placeVote.getPlaceVoteItems().stream()
                    .filter(item -> item.getPlace().equals(placeVote.getPlaceResult()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR));

            Boolean editable = false;

            if(placeVoteItem.getEditable() && entity.getAuthor() == user){
                editable = true;
            }

            place = FindSimplePLaceResponseDto.builder()
                    .value(placeVoteItem.getPlace().toString())
                    .editable(editable.toString())
                    .build();
        }

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
                .date(date)
                .place(place)
                .participantsNum(String.valueOf(participantsNum))
                .participants(participants)
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
}

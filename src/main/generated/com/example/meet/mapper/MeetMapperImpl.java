package com.example.meet.mapper;

import com.example.meet.common.dto.response.meet.CreateMeetResponseDto;
import com.example.meet.common.dto.response.meet.FindMeetSimpleResponseDto;
import com.example.meet.entity.Meet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-17T00:16:48+0900",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 17.0.10 (Oracle Corporation)"
)
public class MeetMapperImpl implements MeetMapper {

    @Override
    public CreateMeetResponseDto entityToCreateDto(Meet entity) {
        if ( entity == null ) {
            return null;
        }

        CreateMeetResponseDto.CreateMeetResponseDtoBuilder createMeetResponseDto = CreateMeetResponseDto.builder();

        createMeetResponseDto.id( entity.getId() );

        return createMeetResponseDto.build();
    }

    @Override
    public ArrayList<FindMeetSimpleResponseDto> EntityListToDtoList(List<Meet> meetList) {
        if ( meetList == null ) {
            return null;
        }

        ArrayList<FindMeetSimpleResponseDto> arrayList = new ArrayList<FindMeetSimpleResponseDto>();
        for ( Meet meet : meetList ) {
            arrayList.add( EntityToDtoSimple( meet ) );
        }

        return arrayList;
    }
}

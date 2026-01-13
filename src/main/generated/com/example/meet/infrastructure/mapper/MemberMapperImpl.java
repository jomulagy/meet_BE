package com.example.meet.infrastructure.mapper;

import com.example.meet.infrastructure.dto.response.member.MemberResponseDto;
import com.example.meet.infrastructure.enumulation.MemberPrevillege;
import com.example.meet.api.member.application.domain.entity.Member;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-13T23:08:19+0900",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 17.0.10 (Oracle Corporation)"
)
public class MemberMapperImpl implements MemberMapper {

    @Override
    public MemberResponseDto entityToDto(Member member) {
        if ( member == null ) {
            return null;
        }

        MemberResponseDto.MemberResponseDtoBuilder memberResponseDto = MemberResponseDto.builder();

        if ( member.getId() != null ) {
            memberResponseDto.id( String.valueOf( member.getId() ) );
        }
        memberResponseDto.name( member.getName() );
        if ( member.getDeposit() != null ) {
            memberResponseDto.deposit( String.valueOf( member.getDeposit() ) );
        }
        if ( member.getPrevillege() != null ) {
            memberResponseDto.previllege( member.getPrevillege().name() );
        }
        memberResponseDto.email( member.getEmail() );

        return memberResponseDto.build();
    }

    @Override
    public Member DtoToEntity(MemberResponseDto memberDto) {
        if ( memberDto == null ) {
            return null;
        }

        Member.MemberBuilder member = Member.builder();

        if ( memberDto.getId() != null ) {
            member.id( Long.parseLong( memberDto.getId() ) );
        }
        member.name( memberDto.getName() );
        member.email( memberDto.getEmail() );
        if ( memberDto.getPrevillege() != null ) {
            member.previllege( Enum.valueOf( MemberPrevillege.class, memberDto.getPrevillege() ) );
        }
        if ( memberDto.getDeposit() != null ) {
            member.deposit( Boolean.parseBoolean( memberDto.getDeposit() ) );
        }

        return member.build();
    }

    @Override
    public List<MemberResponseDto> usersToUserDtos(List<Member> members) {
        if ( members == null ) {
            return null;
        }

        List<MemberResponseDto> list = new ArrayList<MemberResponseDto>( members.size() );
        for ( Member member : members ) {
            list.add( entityToDto( member ) );
        }

        return list;
    }

    @Override
    public List<Member> userDtosToUsers(List<MemberResponseDto> memberDtos) {
        if ( memberDtos == null ) {
            return null;
        }

        List<Member> list = new ArrayList<Member>( memberDtos.size() );
        for ( MemberResponseDto memberResponseDto : memberDtos ) {
            list.add( DtoToEntity( memberResponseDto ) );
        }

        return list;
    }
}

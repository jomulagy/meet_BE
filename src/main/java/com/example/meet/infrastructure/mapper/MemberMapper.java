package com.example.meet.infrastructure.mapper;

import com.example.meet.infrastructure.dto.response.member.MemberResponseDto;
import com.example.meet.api.member.application.domain.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper
public interface MemberMapper {
    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    MemberResponseDto entityToDto(Member member);
    Member DtoToEntity(MemberResponseDto memberDto);

    List<MemberResponseDto> usersToUserDtos(List<Member> members);
    List<Member> userDtosToUsers(List<MemberResponseDto> memberDtos);

}

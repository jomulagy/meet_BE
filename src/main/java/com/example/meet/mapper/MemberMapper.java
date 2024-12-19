package com.example.meet.mapper;

import com.example.meet.common.dto.response.member.MemberResponseDto;
import com.example.meet.entity.Member;
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

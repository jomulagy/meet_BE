package com.example.meet.mapper;

import com.example.meet.common.dto.response.member.MemberResponseDto;
import com.example.meet.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper
public interface MemberMapper {
    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    default MemberResponseDto entityToDto(Member member){
        Boolean deposit = Boolean.FALSE;
        Boolean isFirst = Boolean.FALSE;

        if(member.getDeposit() != null){
            deposit = member.hasDeposit();
        }

        if(member.getUuid() != null){
            isFirst = Boolean.TRUE;
        }
        return MemberResponseDto.builder()
                .id(member.getId().toString())
                .name(member.getName())
                .deposit(deposit.toString())
                .previllege(member.getPrivilege().getStatus().toString())
                .email(member.getEmail())
                .isFirst(isFirst.toString())
                .build();
    }

    List<MemberResponseDto> usersToUserDtos(List<Member> members);

}

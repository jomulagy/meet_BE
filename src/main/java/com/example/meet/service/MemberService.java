package com.example.meet.service;

import com.example.meet.common.dto.request.MemberListRequestDto;
import com.example.meet.common.dto.response.MemberPrevillegeResponseDto;
import com.example.meet.common.dto.response.MemberResponseDto;
import com.example.meet.common.exception.BusinessException;
import com.example.meet.common.variables.ErrorCode;
import com.example.meet.common.variables.MemberPrevillege;
import com.example.meet.entity.Member;
import com.example.meet.mapper.MemberMapper;
import com.example.meet.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberPrevillegeResponseDto searchMemberPrevillege(Long userId) {
        Optional<Member> user = memberRepository.findById(userId);
        if(!user.isPresent()){
            throw new BusinessException(ErrorCode.MEMBER_NOT_EXISTS);
        }
        System.out.println("user.get().getPrevillege() = " + user.get().getPrevillege());
        return new MemberPrevillegeResponseDto(user.get().getPrevillege());

    }

    public List<MemberResponseDto> findMemberList(MemberListRequestDto inDto) {
        //로그인 한 유저 확인
        Member member = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인 (관리자 여부)
        if(!member.getPrevillege().equals(MemberPrevillege.admin)){
            throw new BusinessException(ErrorCode.MEMBER_PERMITION_REQUIRED);
        }
        return MemberMapper.INSTANCE.usersToUserDtos(memberRepository.findAll());
    }
}

package com.example.meet.service;

import com.example.meet.common.dto.request.CreateMeetRequestDto;
import com.example.meet.common.dto.response.CreateMeetResponseDto;
import com.example.meet.common.enumulation.ErrorCode;
import com.example.meet.common.enumulation.MemberPrevillege;
import com.example.meet.common.exception.BusinessException;
import com.example.meet.entity.Meet;
import com.example.meet.entity.Member;
import com.example.meet.mapper.MeetMapper;
import com.example.meet.repository.MeetRepository;
import com.example.meet.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetService {
    private final MeetRepository meetRepository;
    private final MeetMapper meetMapper = MeetMapper.INSTANCE;
    private final MemberRepository memberRepository;

    public CreateMeetResponseDto createMeet(CreateMeetRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인 (관리자, 멤버 여부)
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMITION_REQUIRED);
        }

        Meet entity = meetMapper.dtoToEntity(inDto);

        meetRepository.save(entity);

        return meetMapper.entityToCreateDto(entity);
    }
}

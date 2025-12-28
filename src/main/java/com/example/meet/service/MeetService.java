package com.example.meet.service;

import com.example.meet.entity.Member;
import com.example.meet.infrastructure.dto.request.DeleteMeetRequestDto;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.MemberPrevillege;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.repository.MeetRepository;
import com.example.meet.infrastructure.repository.MemberRepository;
import com.example.meet.post.application.domain.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetService {
    private final MeetRepository meetRepository;
    private final MemberRepository memberRepository;

    public void deleteMeet(DeleteMeetRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인 (관리자, 멤버 여부)
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        //모임 조회
        Post post = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        //작성자 or 관리자 인지 확인
        if(!(user.getPrevillege().equals(MemberPrevillege.admin) || post.getAuthor() == user)){
            throw new BusinessException(ErrorCode.MEET_EDIT_PERMISSION_REQUIRED);
        }

        meetRepository.delete(post);
    }
}

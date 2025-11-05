package com.example.meet.place.application.service;

import com.example.meet.entity.Member;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.MemberPrevillege;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.utils.DateTimeUtils;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.meet.application.port.out.GetMeetPort;
import com.example.meet.member.application.port.out.GetMemberPort;
import com.example.meet.place.adapter.out.dto.request.FindPlaceVoteRequestDto;
import com.example.meet.place.adapter.out.dto.response.FindPlaceVoteResponseDto;
import com.example.meet.place.application.port.in.GetPlaceVoteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetPlaceVoteService implements GetPlaceVoteUseCase {
    private final GetMemberPort getMemberPort;
    private final GetMeetPort getMeetPort;

    @Override
    public FindPlaceVoteResponseDto findPlaceVote(FindPlaceVoteRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = getMemberPort.getMemberById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        Meet meet = getMeetPort.getMeetById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        String endDate = null;
        if(meet.getPlaceVote() != null){
            endDate = DateTimeUtils.formatWithOffset(meet.getEndDate());
        }

        boolean isAuthor = meet.getAuthor().equals(user);

        return FindPlaceVoteResponseDto.builder()
                .meetTitle(meet.getTitle())
                .endDate(endDate)
                .isAuthor(isAuthor)
                .build();
    }
}

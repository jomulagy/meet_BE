package com.example.meet.service;

import com.example.meet.entity.Member;
import com.example.meet.entity.ParticipateVote;
import com.example.meet.entity.ParticipateVoteItem;
import com.example.meet.entity.PlaceVote;
import com.example.meet.entity.ScheduleVote;
import com.example.meet.entity.ScheduleVoteItem;
import com.example.meet.infrastructure.dto.TemplateArgs;
import com.example.meet.meet.adapter.in.dto.CreateMeetRequestDto;
import com.example.meet.infrastructure.dto.request.DeleteMeetRequestDto;
import com.example.meet.infrastructure.dto.request.EditMeetRequestDto;
import com.example.meet.infrastructure.dto.request.FindMeetRequestDto;
import com.example.meet.meet.adapter.in.dto.CreateMeetResponseDto;
import com.example.meet.infrastructure.dto.response.meet.EditMeetResponseDto;
import com.example.meet.infrastructure.dto.response.meet.FindMeetResponseDto;
import com.example.meet.infrastructure.dto.response.meet.FindMeetSimpleResponseDto;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.MemberPrevillege;
import com.example.meet.infrastructure.enumulation.Message;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.mapper.MeetMapper;
import com.example.meet.infrastructure.utils.MessageManager;
import com.example.meet.infrastructure.utils.ScheduleManager;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.repository.MeetRepository;
import com.example.meet.repository.MemberRepository;
import com.example.meet.repository.ParticipateVoteItemRepository;
import com.example.meet.repository.ParticipateVoteRepository;
import com.example.meet.repository.PlaceRepository;
import com.example.meet.repository.PlaceVoteItemRepository;
import com.example.meet.repository.PlaceVoteRepository;
import com.example.meet.repository.ScheduleVoteItemRepository;
import com.example.meet.repository.ScheduleVoteRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetService {
    private final MeetRepository meetRepository;
    private final MeetMapper meetMapper = MeetMapper.INSTANCE;
    private final MemberRepository memberRepository;
    private final ScheduleVoteRepository scheduleVoteRepository;
    private final ScheduleVoteItemRepository scheduleVoteItemRepository;
    private final PlaceVoteRepository placeVoteRepository;
    private final PlaceVoteItemRepository placeVoteItemRepository;
    private final ParticipateVoteRepository participateVoteRepository;
    private final ParticipateVoteItemRepository participateVoteItemRepository;
    private final PlaceRepository placeRepository;

    private final MessageManager messageManager;

    public List<FindMeetSimpleResponseDto> findMeetList(FindMeetRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인 (관리자, 멤버 여부)
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        //모임 조회
        List<Meet> meetList = meetRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return meetMapper.EntityListToDtoList(meetList);
    }

    public FindMeetResponseDto findMeet(FindMeetRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인 (관리자, 멤버 여부)
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        //모임 조회
        Meet meet = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        return meetMapper.EntityToDto(meet, user);
    }

    @Transactional
    public EditMeetResponseDto editMeet(EditMeetRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인 (관리자, 멤버 여부)
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        //모임 조회
        Meet meet = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        //작성자 or 관리자 인지 확인
        if(!(user.getPrevillege().equals(MemberPrevillege.admin) || meet.getAuthor() == user)){
            throw new BusinessException(ErrorCode.MEET_EDIT_PERMISSION_REQUIRED);
        }

        //투표한 필드는 편집 불가(날짜)
        if(meet.getDate() != null && meet.getScheduleVote() != null && meet.getScheduleVote().getDateResult() != null && !Objects.equals(inDto.getDate(), meet.getDate().toLocalDate())){
            throw new BusinessException(ErrorCode.VOTE_REQUIRED);
        }

        //투표한 필드는 편집 불가(시간)
        if(meet.getDate() != null && meet.getScheduleVote() != null && meet.getScheduleVote().getDateResult() != null && !Objects.equals(inDto.getTime(), meet.getDate().toLocalTime())){
            throw new BusinessException(ErrorCode.VOTE_REQUIRED);
        }

        //투표한 필드는 편집 불가(장소)
        if(meet.getPlace() != null && meet.getPlaceVote() != null && meet.getPlaceVote().getPlaceResult() != null && !Objects.equals(inDto.getPlace(), meet.getPlace())){
            throw new BusinessException(ErrorCode.VOTE_REQUIRED);
        }

        meet.update(inDto);

        return meetMapper.EntityToUpdateDto(meet);
    }

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
        Meet meet = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        //작성자 or 관리자 인지 확인
        if(!(user.getPrevillege().equals(MemberPrevillege.admin) || meet.getAuthor() == user)){
            throw new BusinessException(ErrorCode.MEET_EDIT_PERMISSION_REQUIRED);
        }

        meetRepository.delete(meet);
    }
}

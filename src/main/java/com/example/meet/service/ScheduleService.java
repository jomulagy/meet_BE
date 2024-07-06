package com.example.meet.service;

import static java.lang.Long.parseLong;

import com.example.meet.common.CommonResponse;
import com.example.meet.common.dto.request.CreateScheduleVoteItemRequestDto;
import com.example.meet.common.dto.request.DeleteScheduleVoteItemRequestDto;
import com.example.meet.common.dto.request.FindScheduleVoteItemRequestDto;
import com.example.meet.common.dto.request.FindScheduleVoteRequestDto;
import com.example.meet.common.dto.request.FindUserScheduleVoteRequestDto;
import com.example.meet.common.dto.request.UpdateScheduleVoteRequestDto;
import com.example.meet.common.dto.response.DeleteScheduleVoteItemResponseDto;
import com.example.meet.common.dto.response.FindScheduleVoteItemResponseDto;
import com.example.meet.common.dto.response.FindScheduleVoteResponseDto;
import com.example.meet.common.dto.response.FindUserScheduleVoteResponseDto;
import com.example.meet.common.dto.response.SimpleMemberResponseDto;
import com.example.meet.common.dto.response.UserScheduleVoteItemResponseDto;
import com.example.meet.common.enumulation.ErrorCode;
import com.example.meet.common.enumulation.MemberPrevillege;
import com.example.meet.common.exception.BusinessException;
import com.example.meet.common.dto.response.CreateScheduleVoteItemResponseDto;
import com.example.meet.controller.UpdateScheduleVoteResponseDto;
import com.example.meet.entity.Meet;
import com.example.meet.entity.Member;
import com.example.meet.entity.ScheduleVote;
import com.example.meet.entity.ScheduleVoteItem;
import com.example.meet.mapper.ScheduleVoteItemMapper;
import com.example.meet.repository.MeetRepository;
import com.example.meet.repository.MemberRepository;
import com.example.meet.repository.ScheduleVoteItemRepository;
import com.example.meet.repository.ScheduleVoteRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final MemberRepository memberRepository;
    private final MeetRepository meetRepository;
    private final ScheduleVoteRepository scheduleVoteRepository;
    private final ScheduleVoteItemRepository scheduleVoteItemRepository;

    private final ScheduleVoteItemMapper scheduleVoteItemMapper = ScheduleVoteItemMapper.INSTANCE;

    @Scheduled(cron = "0 0 8 2 3,6,9,12 ?")
    @Transactional
    public void terminateScheduleVote(){
        LocalDate currentDate = LocalDate.now();
        List<ScheduleVote> scheduleVoteList = scheduleVoteRepository.findEventsWithNullDateResultAndEndDateBefore(currentDate);

        for(ScheduleVote scheduleVote : scheduleVoteList){
            scheduleVote.setDateResult();
            scheduleVote.getMeet().setDateResult(scheduleVote.getDateResult());
        }
    }
    public List<FindScheduleVoteItemResponseDto> findScheduleVoteItemList(FindScheduleVoteItemRequestDto inDto) {

        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        Meet meet = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        List<ScheduleVoteItem> scheduleVoteItemList = meet.getScheduleVote().getScheduleVoteItems();

        List<FindScheduleVoteItemResponseDto> outDtoList = new ArrayList<>();
        for(ScheduleVoteItem item : scheduleVoteItemList){
            List<SimpleMemberResponseDto> memberList = new ArrayList<>();
            item.getScheduleVoters().forEach(member -> {
                        memberList.add(SimpleMemberResponseDto.builder()
                                        .id(member.getId().toString())
                                        .name(member.getName())
                                        .build());
                    }
            );
            outDtoList.add(
                    FindScheduleVoteItemResponseDto.builder()
                            .id(item.getId().toString())
                            .date(item.getDate().toString())
                            .editable(item.getEditable().toString())
                            .memberList(memberList)
                            .build()
            );

        }

        return outDtoList;
    }

    public FindUserScheduleVoteResponseDto findUserScheduleVoteItemList(FindUserScheduleVoteRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        Meet meet = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        List<ScheduleVoteItem> scheduleVoteItemList = meet.getScheduleVote().getScheduleVoteItems();

        FindUserScheduleVoteResponseDto outDto = new FindUserScheduleVoteResponseDto();
        for(ScheduleVoteItem item : scheduleVoteItemList){
            String isVote = "false";
            if(item.getScheduleVoters().contains(user)){
                isVote = "true";
            }
            UserScheduleVoteItemResponseDto dto = UserScheduleVoteItemResponseDto.builder()
                    .id(item.getId().toString())
                    .date(item.getDate().toString())
                    .isVote(isVote)
                    .build();
            outDto.getUserScheduleVoteItemList().add(dto);
        }

        return outDto;
    }

    public CreateScheduleVoteItemResponseDto createScheduleVoteItem(CreateScheduleVoteItemRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        Meet meet = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        if(meet.getDate() != null){
            throw new BusinessException(ErrorCode.SCHEDULE_VOTE_END);
        }

        List<ScheduleVoteItem> scheduleVoteItemList = meet.getScheduleVote().getScheduleVoteItems();

        //TODO: test
        for(ScheduleVoteItem item : scheduleVoteItemList){
            if(item.getDate().equals(inDto.getDate())){
                throw new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_DUPLICATED);
            }
        }

        ScheduleVoteItem entity = scheduleVoteItemMapper.toEntity(inDto, meet.getScheduleVote());
        ScheduleVoteItem scheduleVoteItem = scheduleVoteItemRepository.save(entity);

        scheduleVoteItemList.add(scheduleVoteItem);
        scheduleVoteRepository.save(meet.getScheduleVote());

        return CreateScheduleVoteItemResponseDto.builder()
                    .scheduleVoteItemId(scheduleVoteItem.getId().toString())
                    .build();
    }

    public DeleteScheduleVoteItemResponseDto deleteScheduleVoteItem(DeleteScheduleVoteItemRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        ScheduleVoteItem scheduleVoteItem = scheduleVoteItemRepository.findById(inDto.getScheduleVoteItemId()).orElseThrow(
                () -> new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_NOT_EXISTS)
        );

        if(scheduleVoteItem.getScheduleVote().getMeet().getDate() != null){
            throw new BusinessException(ErrorCode.SCHEDULE_VOTE_END);
        }

        scheduleVoteItemRepository.delete(scheduleVoteItem);
        return null;
    }

    public UpdateScheduleVoteResponseDto updateScheduleVote(UpdateScheduleVoteRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        Meet meet = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        if(meet.getDate() != null){
            throw new BusinessException(ErrorCode.SCHEDULE_VOTE_END);
        }

        List<ScheduleVoteItem> scheduleVoteItemList = meet.getScheduleVote().getScheduleVoteItems();

        for(Long id : inDto.getScheduleVoteItemList()){
            ScheduleVoteItem scheduleVoteItem = scheduleVoteItemRepository.findById(id).orElseThrow(
                    () -> new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_NOT_EXISTS)
            );
        }

        for(ScheduleVoteItem item : scheduleVoteItemList){
            List<Long> memberIds = item.getScheduleVoters().stream()
                    .map(Member::getId)
                    .collect(Collectors.toList());

            if(memberIds.contains(user.getId()) && !inDto.getScheduleVoteItemList().contains(user.getId())){
                item.getScheduleVoters().removeIf(member -> member.equals(user));
            }
            else if(!memberIds.contains(user.getId()) && inDto.getScheduleVoteItemList().contains(user.getId())){
                item.getScheduleVoters().add(user);
            }
        }

        return null;
    }

    public FindScheduleVoteResponseDto findScheduleVote(FindScheduleVoteRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        Meet meet = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        return FindScheduleVoteResponseDto.builder()
                .meetTitle(meet.getTitle())
                .endDate(meet.getScheduleVote().getEndDate().toString())
                .build();
    }

}

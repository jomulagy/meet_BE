package com.example.meet.service;

import static java.lang.Long.parseLong;

import com.example.meet.common.dto.request.CreateScheduleVoteItemRequestDto;
import com.example.meet.common.dto.request.DeleteScheduleVoteItemRequestDto;
import com.example.meet.common.dto.request.FindScheduleVoteItemRequestDto;
import com.example.meet.common.dto.request.FindScheduleVoteRequestDto;
import com.example.meet.common.dto.request.UpdateScheduleVoteRequestDto;
import com.example.meet.common.dto.response.DeleteScheduleVoteItemResponseDto;
import com.example.meet.common.dto.response.FindScheduleVoteItemResponseDto;
import com.example.meet.common.dto.response.FindScheduleVoteResponseDto;
import com.example.meet.common.dto.response.SimpleMemberResponseDto;
import com.example.meet.common.enumulation.ErrorCode;
import com.example.meet.common.enumulation.MemberPrevillege;
import com.example.meet.common.exception.BusinessException;
import com.example.meet.common.dto.response.CreateScheduleVoteItemResponseDto;
import com.example.meet.common.dto.response.UpdateScheduleVoteResponseDto;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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

    @Scheduled(cron = "0 50 7 2 3,6,9,12 ?")
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
            String isVote = "false";
            if(item.getScheduleVoters().contains(user)){
                isVote = "true";
            }
            List<SimpleMemberResponseDto> memberList = new ArrayList<>();
            item.getScheduleVoters().forEach(member -> {
                        memberList.add(SimpleMemberResponseDto.builder()
                                        .id(member.getId().toString())
                                        .name(member.getName())
                                        .build());
                    }
            );

            //수정 가능 여부 확인
            String editable = "false";
            if(item.getAuthor() == user && item.getEditable() && item.getScheduleVoters().isEmpty()){
                editable = "true";
            }
            outDtoList.add(
                    FindScheduleVoteItemResponseDto.builder()
                            .id(item.getId().toString())
                            .date(item.getDate().toLocalDate().toString())
                            .time(item.getDate().toLocalTime().toString())
                            .editable(editable)
                            .isVote(isVote)
                            .memberList(memberList)
                            .build()
            );

        }

        return outDtoList;
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

        if(meet.getScheduleVote().getEndDate() != null && meet.getScheduleVote().getEndDate().isBefore(LocalDateTime.now())){
            throw new BusinessException(ErrorCode.SCHEDULE_VOTE_END);
        }

        List<ScheduleVoteItem> scheduleVoteItemList = meet.getScheduleVote().getScheduleVoteItems();

        //TODO: test
        for(ScheduleVoteItem item : scheduleVoteItemList){
            if(item.getDate().toLocalDate().equals(inDto.getDate()) && item.getDate().toLocalTime().equals(inDto.getTime())){
                throw new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_DUPLICATED);
            }
        }

        ScheduleVoteItem entity = scheduleVoteItemMapper.toEntity(inDto, meet.getScheduleVote(), user);
        ScheduleVoteItem scheduleVoteItem = scheduleVoteItemRepository.save(entity);

        scheduleVoteItemList.add(scheduleVoteItem);
        scheduleVoteRepository.save(meet.getScheduleVote());

        String isVote = "false";
        if(scheduleVoteItem.getScheduleVoters().contains(user)){
            isVote = "true";
        }
        List<SimpleMemberResponseDto> memberList = new ArrayList<>();
        scheduleVoteItem.getScheduleVoters().forEach(member -> {
                    memberList.add(SimpleMemberResponseDto.builder()
                            .id(member.getId().toString())
                            .name(member.getName())
                            .build());
                }
        );

        return CreateScheduleVoteItemResponseDto.builder()
                    .id(scheduleVoteItem.getId().toString())
                    .date(scheduleVoteItem.getDate().toLocalDate().toString())
                    .time(scheduleVoteItem.getDate().toLocalTime().toString())
                    .editable("true")
                    .isVote(isVote)
                    .memberList(memberList)
                    .build();
    }

    @Transactional
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

        if(scheduleVoteItem.getScheduleVote().getEndDate() != null && scheduleVoteItem.getScheduleVote().getEndDate().isBefore(LocalDateTime.now())){
            throw new BusinessException(ErrorCode.SCHEDULE_VOTE_END);
        }

        scheduleVoteItem.getScheduleVote().getScheduleVoteItems().remove(scheduleVoteItem);
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

        if(meet.getScheduleVote() != null && meet.getScheduleVote().getEndDate() != null && meet.getScheduleVote().getEndDate().isBefore(LocalDateTime.now())){
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
                    .toList();

            if(memberIds.contains(user.getId()) && !inDto.getScheduleVoteItemList().contains(item.getId())){
                item.getScheduleVoters().removeIf(member -> member.equals(user));
            }
            else if(!memberIds.contains(user.getId()) && inDto.getScheduleVoteItemList().contains(item.getId())){
                item.getScheduleVoters().add(user);
            }

            scheduleVoteItemRepository.save(item);
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

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String endDate = meet.getScheduleVote().getEndDate().format(dateTimeFormatter);

        return FindScheduleVoteResponseDto.builder()
                .meetTitle(meet.getTitle())
                .endDate(endDate)
                .build();
    }

}

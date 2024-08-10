package com.example.meet.service;

import com.example.meet.common.dto.request.participate.FindParticipateVoteItemRequestDto;
import com.example.meet.common.dto.request.participate.FindParticipateVoteRequestDto;
import com.example.meet.common.dto.response.SimpleMemberResponseDto;
import com.example.meet.common.dto.response.participate.FindParticipateVoteItemResponseDto;
import com.example.meet.common.dto.response.participate.FindParticipateVoteResponseDto;
import com.example.meet.common.dto.response.participate.UpdateParticipateVoteResponseDto;
import com.example.meet.common.dto.response.place.FindPlaceVoteItemResponseDto;
import com.example.meet.common.dto.response.place.FindPlaceVoteResponseDto;
import com.example.meet.common.enumulation.ErrorCode;
import com.example.meet.common.enumulation.MemberPrevillege;
import com.example.meet.common.exception.BusinessException;
import com.example.meet.controller.UpdateParticipateVoteRequestDto;
import com.example.meet.entity.Meet;
import com.example.meet.entity.Member;
import com.example.meet.entity.ParticipateVote;
import com.example.meet.entity.ParticipateVoteItem;
import com.example.meet.entity.PlaceVoteItem;
import com.example.meet.repository.MeetRepository;
import com.example.meet.repository.MemberRepository;
import com.example.meet.repository.ParticipateVoteItemRepository;
import com.example.meet.repository.ParticipateVoteRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class ParticipateService {
    private final ParticipateVoteRepository participateVoteRepository;
    private final ParticipateVoteItemRepository participateVoteItemRepository;
    private final MemberRepository memberRepository;
    private final MeetRepository meetRepository;

    @Scheduled(cron = "0 0 8 4 3,6,9,12 ?")
    @Transactional
    public void terminatePlaceVote(){
        LocalDate currentDate = LocalDate.now();
        List<ParticipateVote> placeVoteList = participateVoteRepository.findByEndDateBefore(currentDate);

        for(ParticipateVote participateVote : placeVoteList){
            participateVote.setTotalNum();
            participateVote.getMeet().setParticipantsNum(participateVote.getTotalNum());
        }
    }

    public FindParticipateVoteResponseDto findParticipateVote(FindParticipateVoteRequestDto inDto) {
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

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String endDate = meet.getParticipateVote().getEndDate().format(dateTimeFormatter);
        return FindParticipateVoteResponseDto.builder()
                .meetTitle(meet.getTitle())
                .endDate(endDate)
                .build();
    }

    public List<FindParticipateVoteItemResponseDto> findParticipateVoteItemList(FindParticipateVoteItemRequestDto inDto) {
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

        List<ParticipateVoteItem> participateVoteList = meet.getParticipateVote().getParticipateVoteItems();

        List<FindParticipateVoteItemResponseDto> outDtoList = new ArrayList<>();
        for(ParticipateVoteItem item : participateVoteList){
            String isVote = "false";
            if(item.getParticipateVoters().contains(user)){
                isVote = "true";
            }
            List<SimpleMemberResponseDto> memberList = new ArrayList<>();
            item.getParticipateVoters().forEach(member -> {
                        memberList.add(SimpleMemberResponseDto.builder()
                                .id(member.getId().toString())
                                .name(member.getName())
                                .build());
                    }
            );
        }

        return outDtoList;
    }

    public UpdateParticipateVoteResponseDto updateParticipateVote(UpdateParticipateVoteRequestDto inDto) {
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

        if(meet.getPlaceVote().getEndDate().isBefore(LocalDateTime.now())){
            throw new BusinessException(ErrorCode.PARTICIPATE_VOTE_END);
        }

        List<ParticipateVoteItem> participateVoteItemList = meet.getParticipateVote().getParticipateVoteItems();

        for(Long id : inDto.getParticipateVoteItemIdList()){
            ParticipateVoteItem participateVoteItem = participateVoteItemRepository.findById(id).orElseThrow(
                    () -> new BusinessException(ErrorCode.PARTICIPAT_VOTE_ITEM_NOT_EXISTS)
            );
        }

        for(ParticipateVoteItem item : participateVoteItemList){
            List<Long> memberIds = item.getParticipateVoters().stream()
                    .map(Member::getId)
                    .toList();

            if(memberIds.contains(user.getId()) && !inDto.getParticipateVoteItemIdList().contains(item.getId())){
                item.getParticipateVoters().removeIf(member -> member.equals(user));
            }
            else if(!memberIds.contains(user.getId()) && inDto.getParticipateVoteItemIdList().contains(item.getId())){
                item.getParticipateVoters().add(user);
            }

            participateVoteItemRepository.save(item);
        }

        return null;
    }
}

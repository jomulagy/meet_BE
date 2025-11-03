package com.example.meet.service;

import com.example.meet.infrastructure.dto.request.participate.FindParticipateVoteItemRequestDto;
import com.example.meet.infrastructure.dto.request.participate.FindParticipateVoteRequestDto;
import com.example.meet.infrastructure.dto.response.member.SimpleMemberResponseDto;
import com.example.meet.infrastructure.dto.response.participate.FindParticipateVoteItemResponseDto;
import com.example.meet.infrastructure.dto.response.participate.FindParticipateVoteResponseDto;
import com.example.meet.infrastructure.dto.response.participate.UpdateParticipateVoteResponseDto;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.MemberPrevillege;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.dto.request.participate.UpdateParticipateVoteRequestDto;
import com.example.meet.infrastructure.utils.DateTimeUtils;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.entity.Member;
import com.example.meet.entity.ParticipateVoteItem;
import com.example.meet.infrastructure.repository.MeetRepository;
import com.example.meet.infrastructure.repository.MemberRepository;
import com.example.meet.infrastructure.repository.ParticipateVoteItemRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipateService {
    private final ParticipateVoteItemRepository participateVoteItemRepository;
    private final MemberRepository memberRepository;
    private final MeetRepository meetRepository;

    public FindParticipateVoteResponseDto findParticipateVote(FindParticipateVoteRequestDto inDto) {
        String endDate = null;
        String date = null;
        String time = null;

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

        if( meet.getParticipateVote().getEndDate() != null){
            endDate = DateTimeUtils.formatWithOffset(meet.getParticipateVote().getEndDate());
        }

        if( meet.getDate() != null){
            date = meet.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            time = meet.getDate().format(DateTimeFormatter.ofPattern("HH시 mm분"));
        }

        Boolean isAuthor = meet.getAuthor().equals(user);

        return FindParticipateVoteResponseDto.builder()
                .meetTitle(meet.getTitle())
                .date(date)
                .time(time)
                .place(meet.getPlace())
                .endDate(endDate)
                .isAuthor(isAuthor.toString())
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

            String name = item.getIsParticipate().equals(true) ? "참여" : "불참";

            outDtoList.add(
                    FindParticipateVoteItemResponseDto.builder()
                            .id(item.getId().toString())
                            .name(name)
                            .isVote(isVote)
                            .memberList(memberList)
                            .build()
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

        if(meet.getParticipateVote().getEndDate().isBefore(LocalDateTime.now())){
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

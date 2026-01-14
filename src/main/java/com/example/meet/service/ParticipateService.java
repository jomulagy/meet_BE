package com.example.meet.service;

import com.example.meet.infrastructure.dto.response.participate.UpdateParticipateVoteResponseDto;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.MemberRole;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.api.participate.adapter.in.dto.in.UpdateParticipateVoteRequestDto;
import com.example.meet.api.post.application.domain.entity.Post;
import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.api.participate.application.domain.entity.ParticipateVoteItem;
import com.example.meet.infrastructure.repository.MeetRepository;
import com.example.meet.infrastructure.repository.MemberRepository;
import com.example.meet.infrastructure.repository.ParticipateVoteItemRepository;
import java.time.LocalDateTime;
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

    public UpdateParticipateVoteResponseDto updateParticipateVote(UpdateParticipateVoteRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인
        if(user.getRole().equals(MemberRole.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        Post post = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        if(post.getParticipateVote().getEndDate().isBefore(LocalDateTime.now())){
            throw new BusinessException(ErrorCode.PARTICIPATE_VOTE_END);
        }

        List<ParticipateVoteItem> participateVoteItemList = post.getParticipateVote().getParticipateVoteItems();

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

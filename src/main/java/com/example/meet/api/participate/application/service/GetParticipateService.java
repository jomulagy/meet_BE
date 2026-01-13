package com.example.meet.api.participate.application.service;

import com.example.meet.api.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.api.participate.adapter.in.dto.in.FindParticipateVoteRequestDto;
import com.example.meet.infrastructure.dto.response.participate.FindParticipateVoteItemResponseDto;
import com.example.meet.infrastructure.dto.response.participate.FindParticipateVoteResponseDto;
import com.example.meet.infrastructure.utils.DateTimeUtils;
import com.example.meet.api.participate.application.domain.entity.ParticipateVote;
import com.example.meet.api.participate.application.domain.entity.ParticipateVoteItem;
import com.example.meet.api.participate.application.port.in.GetParticipateUseCase;
import com.example.meet.api.post.application.domain.entity.Post;
import com.example.meet.api.post.application.port.in.GetPostUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetParticipateService implements GetParticipateUseCase {
    private final GetLogginedInfoUseCase getLogginedInfoUseCase;
    private final GetPostUseCase getPostUseCase;

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public FindParticipateVoteResponseDto get(FindParticipateVoteRequestDto inDto) {
        Post post = getPostUseCase.getEntity(inDto.getPostId());

        if(post.getParticipateVote() == null) {
            return null;
        }

        Member user = getLogginedInfoUseCase.get();
        ParticipateVote vote = post.getParticipateVote();

        String endDate = null;

        if(vote.getEndDate() != null){
            endDate = DateTimeUtils.formatWithOffset(post.getParticipateVote().getEndDate());
        }

        boolean isVoted = false;
        List<FindParticipateVoteItemResponseDto> itemList = new ArrayList<>();
        List<String> participants = new ArrayList<>();



        if(vote.isActive()) {
            for(ParticipateVoteItem item : vote.getParticipateVoteItems()) {
                if(item.getParticipateVoters().contains(user)) isVoted = true;

                itemList.add(
                        FindParticipateVoteItemResponseDto.builder()
                                .id(item.getId())
                                .name(item.getIsParticipate() ? "참여" : "불참")
                                .isVote(item.getParticipateVoters().contains(user))
                                .memberList(item.getParticipateVoters().stream().map(Member::getName).toList())
                                .build()
                );
            }
        } else {
            Optional<ParticipateVoteItem> item = vote.getParticipateVoteItems().stream().filter(ParticipateVoteItem::getIsParticipate).findFirst();

            if(item.isPresent()) {
                participants = item.get().getParticipateVoters().stream().map(Member::getName).toList();
            }

        }

        return FindParticipateVoteResponseDto.builder()
                .id(vote.getId())
                .endDate(endDate)
                .isActive(vote.isActive())
                .isVoted(isVoted)
                .itemList(itemList)
                .participants(participants)
                .build();
    }
}

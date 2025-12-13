package com.example.meet.vote.application.service;

import com.example.meet.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.entity.Member;
import com.example.meet.infrastructure.dto.response.member.SimpleMemberResponseDto;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.meet.application.port.in.GetMeetUseCase;
import com.example.meet.vote.adapter.in.dto.in.CreateVoteItemRequestDto;
import com.example.meet.vote.adapter.in.dto.out.CreateVoteItemResponseDto;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.domain.entity.VoteItem;
import com.example.meet.vote.application.port.in.CreateVoteItemUseCase;
import com.example.meet.vote.application.port.in.GetVoteUseCase;
import com.example.meet.vote.application.port.out.CreateVotePort;
import com.example.meet.vote.application.port.out.GetVoteItemPort;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateVoteItemService implements CreateVoteItemUseCase {

    private final GetLogginedInfoUseCase getLogginedInfoUseCase;
    private final GetMeetUseCase getMeetUseCase;
    private final GetVoteUseCase getVoteUseCase;
    private final CreateVotePort createVotePort;
    private final GetVoteItemPort getVoteItemPort;

    @Override
    @Transactional
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public CreateVoteItemResponseDto createItem(CreateVoteItemRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();
        Meet meet = getMeetUseCase.get(inDto.getMeetId());
        Vote vote = getVoteUseCase.getActiveVote(meet);

        vote.getVoteItems().stream()
                .filter(item -> item.getContent().equals(inDto.getContent()))
                .findAny()
                .ifPresent(item -> {
                    throw new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_DUPLICATED);
                });

        VoteItem voteItem = VoteItem.builder()
                .content(inDto.getContent())
                .editable(Boolean.TRUE)
                .author(user)
                .vote(vote)
                .build();
        voteItem.getVoters().add(user);

        VoteItem saved = getVoteItemPort.save(voteItem);
        vote.getVoteItems().add(saved);
        createVotePort.create(vote);

        List<SimpleMemberResponseDto> memberList = new ArrayList<>();
        saved.getVoters().forEach(member -> memberList.add(
                SimpleMemberResponseDto.builder()
                        .id(member.getId().toString())
                        .name(member.getName())
                        .build()
        ));

        return CreateVoteItemResponseDto.builder()
                .id(saved.getId().toString())
                .content(saved.getContent())
                .editable("true")
                .isVote("true")
                .memberList(memberList)
                .build();
    }
}

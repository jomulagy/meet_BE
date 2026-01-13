package com.example.meet.api.vote.application.service;

import com.example.meet.api.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.MemberPrevillege;
import com.example.meet.infrastructure.enumulation.VoteType;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.utils.DateTimeUtils;
import com.example.meet.api.vote.adapter.in.dto.in.CreateVoteItemRequestDto;
import com.example.meet.api.vote.adapter.in.dto.out.FindVoteItemResponseDto;
import com.example.meet.api.vote.adapter.in.dto.out.FindVoteResponseDto;
import com.example.meet.api.vote.application.domain.entity.Vote;
import com.example.meet.api.vote.application.domain.entity.VoteItem;
import com.example.meet.api.vote.application.port.in.CreateVoteItemUseCase;
import com.example.meet.api.vote.application.port.in.GetVoteUseCase;
import com.example.meet.api.vote.application.port.out.CreateVoteItemPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateVoteItemService implements CreateVoteItemUseCase {
    private final GetLogginedInfoUseCase getLogginedInfoUseCase;
    private final GetVoteUseCase getVoteUseCase;
    private final CreateVoteItemPort createVoteItemPort;

    @Override
    @Transactional
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public FindVoteResponseDto createItem(CreateVoteItemRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();
        Vote vote = getVoteUseCase.getVote(inDto.getVoteId());

        List<FindVoteItemResponseDto> voteItemResponseDtoList = new ArrayList<>();

        if(inDto.getValue() == null) throw new BusinessException(ErrorCode.VALUE_REQUIRED);

        vote.getVoteItems().stream()
                .filter(item -> inDto.getValue().equals(item.getContent()))
                .findAny()
                .ifPresent(item -> {
                    throw new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_DUPLICATED);
                });

        VoteItem entity = VoteItem.builder()
                .content(inDto.getValue())
                .vote(vote)
                .author(user)
                .build();

        createVoteItemPort.create(entity);

        String endDate = vote.getEndDate() != null ? DateTimeUtils.formatWithOffset(vote.getEndDate()) : null;

        for(VoteItem voteItem : vote.getVoteItems()) {
            List<Member> voters = voteItem.getVoters() == null ? List.of() : voteItem.getVoters();

            voteItemResponseDtoList.add(
                    FindVoteItemResponseDto.builder()
                            .id(voteItem.getId())
                            .value(voteItem.getContent())
                            .isVoted(voters.contains(user))
                            .editable(voteItem.getAuthor().equals(user) || user.getPrevillege().equals(MemberPrevillege.admin))
                            .voterList(voters.stream().map(Member::getName).toList())
                            .build()
            );
        }

        return FindVoteResponseDto.builder()
                .id(vote.getId())
                .title(vote.getTitle())
                .endDate(endDate)
                .isDuplicate(vote.isDuplicate())
                .isActive(vote.getActiveYn())
                .isVoted(false)
                .type(VoteType.of(vote.getType()))
                .result(vote.getResult())
                .itemList(voteItemResponseDtoList)
                .build();
    }
}

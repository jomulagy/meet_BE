package com.example.meet.vote.application.service;

import com.example.meet.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.entity.Member;
import com.example.meet.infrastructure.dto.response.member.SimpleMemberResponseDto;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.vote.adapter.in.dto.in.CreateVoteItemRequestDto;
import com.example.meet.vote.adapter.in.dto.in.DeleteVoteItemRequestDto;
import com.example.meet.vote.adapter.in.dto.in.FindVoteItemRequestDto;
import com.example.meet.vote.adapter.in.dto.out.CreateVoteItemResponseDto;
import com.example.meet.vote.adapter.in.dto.out.DeleteVoteItemResponseDto;
import com.example.meet.vote.adapter.in.dto.out.FindVoteItemResponseDto;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.domain.entity.VoteItem;
import com.example.meet.vote.application.port.in.VoteItemUseCase;
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
public class VoteItemService implements VoteItemUseCase {
    private final GetLogginedInfoUseCase getLogginedInfoUseCase;
    private final ScheduleVoteHelper scheduleVoteHelper;
    private final CreateVotePort createVotePort;
    private final GetVoteItemPort getVoteItemPort;

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public List<FindVoteItemResponseDto> getItemList(FindVoteItemRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();
        Meet meet = scheduleVoteHelper.getMeet(inDto.getMeetId());
        Vote vote = scheduleVoteHelper.getVote(meet);

        List<FindVoteItemResponseDto> responseList = new ArrayList<>();

        for (VoteItem item : vote.getVoteItems()) {
            String isVote = item.getVoters().contains(user) ? "true" : "false";
            String editable = (item.getAuthor().equals(user) && Boolean.TRUE.equals(item.getEditable()) && item.getVoters().isEmpty())
                    ? "true"
                    : "false";

            List<SimpleMemberResponseDto> memberList = new ArrayList<>();
            item.getVoters().forEach(member -> memberList.add(
                    SimpleMemberResponseDto.builder()
                            .id(member.getId().toString())
                            .name(member.getName())
                            .build()
            ));

            responseList.add(FindVoteItemResponseDto.builder()
                    .id(item.getId().toString())
                    .content(item.getContent())
                    .editable(editable)
                    .isVote(isVote)
                    .memberList(memberList)
                    .build());
        }

        return responseList;
    }

    @Override
    @Transactional
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public CreateVoteItemResponseDto createItem(CreateVoteItemRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();
        Meet meet = scheduleVoteHelper.getMeet(inDto.getMeetId());
        Vote vote = scheduleVoteHelper.getVote(meet);

        scheduleVoteHelper.validateVoteIsActive(meet);

        vote.getVoteItems().stream()
                .filter(item -> item.getContent().equals(inDto.getContent()))
                .findAny()
                .ifPresent(item -> { throw new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_DUPLICATED); });

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

    @Override
    @Transactional
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public DeleteVoteItemResponseDto deleteItem(DeleteVoteItemRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();
        VoteItem voteItem = getVoteItemPort.get(inDto.getVoteItemId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_NOT_EXISTS));

        scheduleVoteHelper.validateVoteIsActive(voteItem.getVote().getMeet());

        if (!voteItem.getAuthor().equals(user) || !Boolean.TRUE.equals(voteItem.getEditable()) || !voteItem.getVoters().isEmpty()) {
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        voteItem.getVote().getVoteItems().remove(voteItem);
        getVoteItemPort.delete(voteItem);

        return DeleteVoteItemResponseDto.builder()
                .deletedId(voteItem.getId().toString())
                .build();
    }
}

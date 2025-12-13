package com.example.meet.vote.application.service;

import com.example.meet.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.entity.Member;
import com.example.meet.infrastructure.dto.response.member.SimpleMemberResponseDto;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.utils.DateTimeUtils;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.meet.application.port.out.GetMeetPort;
import com.example.meet.vote.adapter.in.dto.in.CreateVoteItemRequestDto;
import com.example.meet.vote.adapter.in.dto.in.DeleteVoteItemRequestDto;
import com.example.meet.vote.adapter.in.dto.in.FindVoteItemRequestDto;
import com.example.meet.vote.adapter.in.dto.in.FindVoteRequestDto;
import com.example.meet.vote.adapter.in.dto.in.UpdateVoteItemRequestDto;
import com.example.meet.vote.adapter.in.dto.in.UpdateVoteRequestDto;
import com.example.meet.vote.adapter.in.dto.out.CreateVoteItemResponseDto;
import com.example.meet.vote.adapter.in.dto.out.DeleteVoteItemResponseDto;
import com.example.meet.vote.adapter.in.dto.out.FindVoteItemResponseDto;
import com.example.meet.vote.adapter.in.dto.out.FindVoteResponseDto;
import com.example.meet.vote.adapter.in.dto.out.UpdateVoteResponseDto;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.domain.entity.VoteItem;
import com.example.meet.vote.application.port.in.ScheduleVoteUseCase;
import com.example.meet.vote.application.port.out.CreateVotePort;
import com.example.meet.vote.application.port.out.GetVoteItemPort;
import com.example.meet.vote.application.port.out.GetVotePort;
import com.example.meet.vote.application.port.out.UpdateVotePort;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleVoteService implements ScheduleVoteUseCase {
    private final GetLogginedInfoUseCase getLogginedInfoUseCase;
    private final GetMeetPort getMeetPort;
    private final GetVotePort getVotePort;
    private final CreateVotePort createVotePort;
    private final GetVoteItemPort getVoteItemPort;
    private final UpdateVotePort updateVotePort;

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public FindVoteResponseDto get(FindVoteRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();
        Meet meet = getMeet(inDto.getMeetId());
        Vote vote = getVote(meet);

        String endDate = vote.getEndDate() != null ? DateTimeUtils.formatWithOffset(vote.getEndDate()) : null;
        boolean isAuthor = meet.getAuthor().equals(user);

        return FindVoteResponseDto.builder()
                .meetTitle(meet.getTitle())
                .endDate(endDate)
                .isAuthor(Boolean.toString(isAuthor))
                .build();
    }

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public List<FindVoteItemResponseDto> getItemList(FindVoteItemRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();
        Meet meet = getMeet(inDto.getMeetId());
        Vote vote = getVote(meet);

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
        Meet meet = getMeet(inDto.getMeetId());
        Vote vote = getVote(meet);

        validateVoteIsActive(meet);

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

        validateVoteIsActive(voteItem.getVote().getMeet());

        if (!voteItem.getAuthor().equals(user) || !Boolean.TRUE.equals(voteItem.getEditable()) || !voteItem.getVoters().isEmpty()) {
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        voteItem.getVote().getVoteItems().remove(voteItem);
        getVoteItemPort.delete(voteItem);

        return DeleteVoteItemResponseDto.builder()
                .deletedId(voteItem.getId().toString())
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public UpdateVoteResponseDto vote(UpdateVoteRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();
        Meet meet = getMeet(inDto.getMeetId());
        Vote vote = getVote(meet);

        validateVoteIsActive(meet);

        for (UpdateVoteItemRequestDto voteItem : inDto.getVoteItems()) {
            getVoteItemPort.get(voteItem.getVoteItemId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_NOT_EXISTS));
        }

        List<UpdateVoteItemRequestDto> voteItemList = inDto.getVoteItems().stream()
                .map(item -> new UpdateVoteItemRequestDto(item.getVoteItemId(), item.isVote()))
                .toList();

        updateVotePort.updateVoteItems(vote.getId(), user, voteItemList);

        return UpdateVoteResponseDto.builder()
                .status("success")
                .build();
    }

    private Meet getMeet(Long meetId) {
        return getMeetPort.getMeetById(meetId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEET_NOT_EXISTS));
    }

    private Vote getVote(Meet meet) {
        return getVotePort.getByMeetId(meet.getId()).orElse(null);
    }

    private void validateVoteIsActive(Meet meet) {
        LocalDateTime endDate = meet.getEndDate();
        if (endDate != null && endDate.isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.SCHEDULE_VOTE_END);
        }
    }
}

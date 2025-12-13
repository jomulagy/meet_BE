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
import com.example.meet.vote.adapter.in.dto.in.UpdateVoteRequestDto;
import com.example.meet.vote.adapter.in.dto.out.CreateVoteItemResponseDto;
import com.example.meet.vote.adapter.in.dto.out.DeleteVoteItemResponseDto;
import com.example.meet.vote.adapter.in.dto.out.FindVoteItemResponseDto;
import com.example.meet.vote.adapter.in.dto.out.FindVoteResponseDto;
import com.example.meet.vote.adapter.in.dto.out.UpdateVoteResponseDto;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.domain.entity.VoteItem;
import com.example.meet.vote.application.port.in.ScheduleVoteUseCase;
import com.example.meet.vote.application.port.out.VoteItemRepositoryPort;
import com.example.meet.vote.application.port.out.VoteRepositoryPort;
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
    private final VoteRepositoryPort voteRepositoryPort;
    private final VoteItemRepositoryPort voteItemRepositoryPort;

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public FindVoteResponseDto get(FindVoteRequestDto inDto) {
        Member user = getLogginedInfoUseCase.getCurrentMember();
        Meet meet = findMeet(inDto.getMeetId());
        Vote vote = getOrCreateVote(meet);

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
        Member user = getLogginedInfoUseCase.getCurrentMember();
        Meet meet = findMeet(inDto.getMeetId());
        Vote vote = getOrCreateVote(meet);

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
        Member user = getLogginedInfoUseCase.getCurrentMember();
        Meet meet = findMeet(inDto.getMeetId());
        Vote vote = getOrCreateVote(meet);

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

        VoteItem saved = voteItemRepositoryPort.save(voteItem);
        vote.getVoteItems().add(saved);
        voteRepositoryPort.save(vote);

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
        Member user = getLogginedInfoUseCase.getCurrentMember();
        VoteItem voteItem = voteItemRepositoryPort.findById(inDto.getVoteItemId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_NOT_EXISTS));

        validateVoteIsActive(voteItem.getVote().getMeet());

        if (!voteItem.getAuthor().equals(user) || !Boolean.TRUE.equals(voteItem.getEditable()) || !voteItem.getVoters().isEmpty()) {
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        voteItem.getVote().getVoteItems().remove(voteItem);
        voteItemRepositoryPort.delete(voteItem);

        return DeleteVoteItemResponseDto.builder()
                .deletedId(voteItem.getId().toString())
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public UpdateVoteResponseDto update(UpdateVoteRequestDto inDto) {
        Member user = getLogginedInfoUseCase.getCurrentMember();
        Meet meet = findMeet(inDto.getMeetId());
        Vote vote = getOrCreateVote(meet);

        validateVoteIsActive(meet);

        for (Long id : inDto.getVoteItemIdList()) {
            voteItemRepositoryPort.findById(id)
                    .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_NOT_EXISTS));
        }

        for (VoteItem item : vote.getVoteItems()) {
            List<Long> memberIds = item.getVoters().stream().map(Member::getId).toList();
            boolean containsUser = memberIds.contains(user.getId());
            boolean shouldContain = inDto.getVoteItemIdList().contains(item.getId());

            if (containsUser && !shouldContain) {
                item.getVoters().removeIf(member -> member.equals(user));
            } else if (!containsUser && shouldContain) {
                item.getVoters().add(user);
            }

            voteItemRepositoryPort.save(item);
        }

        return UpdateVoteResponseDto.builder()
                .status("success")
                .build();
    }

    private Meet findMeet(Long meetId) {
        return getMeetPort.getMeetById(meetId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEET_NOT_EXISTS));
    }

    private Vote getOrCreateVote(Meet meet) {
        return voteRepositoryPort.findByMeetId(meet.getId())
                .orElseGet(() -> voteRepositoryPort.save(Vote.builder()
                        .meet(meet)
                        .endDate(meet.getEndDate())
                        .activeYn(Boolean.TRUE)
                        .build()));
    }

    private void validateVoteIsActive(Meet meet) {
        LocalDateTime endDate = meet.getEndDate();
        if (endDate != null && endDate.isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.SCHEDULE_VOTE_END);
        }
    }
}

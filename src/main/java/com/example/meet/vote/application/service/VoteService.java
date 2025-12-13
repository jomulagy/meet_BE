package com.example.meet.vote.application.service;

import com.example.meet.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.entity.Member;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.utils.DateTimeUtils;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.vote.adapter.in.dto.in.FindVoteRequestDto;
import com.example.meet.vote.adapter.in.dto.in.UpdateVoteRequestDto;
import com.example.meet.vote.adapter.in.dto.out.FindVoteResponseDto;
import com.example.meet.vote.adapter.in.dto.out.UpdateVoteResponseDto;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.port.in.VoteUseCase;
import com.example.meet.vote.application.port.out.GetVoteItemPort;
import com.example.meet.vote.application.port.out.UpdateVotePort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteService implements VoteUseCase {
    private final GetLogginedInfoUseCase getLogginedInfoUseCase;
    private final ScheduleVoteHelper scheduleVoteHelper;
    private final GetVoteItemPort getVoteItemPort;
    private final UpdateVotePort updateVotePort;

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public FindVoteResponseDto get(FindVoteRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();
        Meet meet = scheduleVoteHelper.getMeet(inDto.getMeetId());
        Vote vote = scheduleVoteHelper.getVote(meet);

        String endDate = vote.getEndDate() != null ? DateTimeUtils.formatWithOffset(vote.getEndDate()) : null;
        boolean isAuthor = meet.getAuthor().equals(user);

        return FindVoteResponseDto.builder()
                .meetTitle(meet.getTitle())
                .endDate(endDate)
                .isAuthor(Boolean.toString(isAuthor))
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public UpdateVoteResponseDto vote(UpdateVoteRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();
        Meet meet = scheduleVoteHelper.getMeet(inDto.getMeetId());
        Vote vote = scheduleVoteHelper.getVote(meet);

        scheduleVoteHelper.validateVoteIsActive(meet);

        inDto.getVoteItems().forEach(voteItem -> getVoteItemPort.get(voteItem.getVoteItemId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_NOT_EXISTS)));

        List<UpdateVotePort.UpdateVoteItemCommand> commands = inDto.getVoteItems().stream()
                .map(item -> new UpdateVotePort.UpdateVoteItemCommand(item.getVoteItemId(), item.getIsVote()))
                .toList();

        updateVotePort.updateVoteItems(vote.getId(), user, commands);

        return UpdateVoteResponseDto.builder()
                .status("success")
                .build();
    }
}

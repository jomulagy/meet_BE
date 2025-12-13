package com.example.meet.vote.application.service;

import com.example.meet.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.entity.Member;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.meet.application.port.in.GetMeetUseCase;
import com.example.meet.vote.adapter.in.dto.in.UpdateVoteItemRequestDto;
import com.example.meet.vote.adapter.in.dto.in.UpdateVoteRequestDto;
import com.example.meet.vote.adapter.in.dto.out.UpdateVoteResponseDto;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.port.in.GetVoteUseCase;
import com.example.meet.vote.application.port.in.UpdateVoteUseCase;
import com.example.meet.vote.application.port.out.GetVoteItemPort;
import com.example.meet.vote.application.port.out.UpdateVotePort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateVoteService implements UpdateVoteUseCase {

    private final GetLogginedInfoUseCase getLogginedInfoUseCase;
    private final GetMeetUseCase getMeetUseCase;
    private final GetVoteUseCase getVoteUseCase;
    private final GetVoteItemPort getVoteItemPort;
    private final UpdateVotePort updateVotePort;

    @Override
    @Transactional
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public UpdateVoteResponseDto vote(UpdateVoteRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();
        Meet meet = getMeetUseCase.get(inDto.getMeetId());
        Vote vote = getVoteUseCase.getActiveVote(meet);

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
}

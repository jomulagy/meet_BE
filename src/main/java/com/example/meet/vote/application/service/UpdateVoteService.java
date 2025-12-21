package com.example.meet.vote.application.service;

import com.example.meet.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.entity.Member;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.post.application.port.in.GetPostUseCase;
import com.example.meet.vote.adapter.in.dto.in.UpdateVoteRequestDto;
import com.example.meet.vote.adapter.in.dto.out.UpdateVoteResponseDto;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.port.in.GetVoteUseCase;
import com.example.meet.vote.application.port.in.UpdateVoteUseCase;
import com.example.meet.vote.application.port.out.GetVoteItemPort;
import com.example.meet.vote.application.port.out.UpdateVotePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateVoteService implements UpdateVoteUseCase {

    private final GetLogginedInfoUseCase getLogginedInfoUseCase;
    private final GetPostUseCase getPostUseCase;
    private final GetVoteUseCase getVoteUseCase;
    private final GetVoteItemPort getVoteItemPort;
    private final UpdateVotePort updateVotePort;

    @Override
    @Transactional
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public UpdateVoteResponseDto vote(UpdateVoteRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();
        Vote vote = getVoteUseCase.getVote(inDto.getVoteId());

        for (Long voteItemId : inDto.getVotedItemIdList()) {
            getVoteItemPort.get(voteItemId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_NOT_EXISTS));
        }

        updateVotePort.updateVoters(vote.getId(), user, inDto.getVotedItemIdList());

        return UpdateVoteResponseDto.builder()
                .status("success")
                .build();
    }
}

package com.example.meet.vote.application.service;

import com.example.meet.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.entity.Member;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.MemberPrevillege;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.vote.adapter.in.dto.in.DeleteVoteItemRequestDto;
import com.example.meet.vote.adapter.in.dto.out.DeleteVoteItemResponseDto;
import com.example.meet.vote.application.domain.entity.VoteItem;
import com.example.meet.vote.application.port.in.DeleteVoteItemUseCase;
import com.example.meet.vote.application.port.in.GetVoteUseCase;
import com.example.meet.vote.application.port.out.GetVoteItemPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteVoteItemService implements DeleteVoteItemUseCase {

    private final GetLogginedInfoUseCase getLogginedInfoUseCase;
    private final GetVoteUseCase getVoteUseCase;
    private final GetVoteItemPort getVoteItemPort;

    @Override
    @Transactional
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public DeleteVoteItemResponseDto deleteItem(DeleteVoteItemRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();
        VoteItem voteItem = getVoteItemPort.get(inDto.getVoteItemId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_NOT_EXISTS));

        getVoteUseCase.getVote(voteItem.getVote().getId());

        if (!user.getPrevillege().equals(MemberPrevillege.admin) && !voteItem.getAuthor().equals(user)) {
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        voteItem.getVote().getVoteItems().remove(voteItem);
        getVoteItemPort.delete(voteItem);

        return DeleteVoteItemResponseDto.builder()
                .deletedId(voteItem.getId().toString())
                .build();
    }
}

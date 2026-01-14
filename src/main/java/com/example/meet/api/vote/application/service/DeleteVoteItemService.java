package com.example.meet.api.vote.application.service;

import com.example.meet.api.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.MemberRole;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.api.vote.adapter.in.dto.in.DeleteVoteItemRequestDto;
import com.example.meet.api.vote.adapter.in.dto.out.DeleteVoteItemResponseDto;
import com.example.meet.api.vote.application.domain.entity.VoteItem;
import com.example.meet.api.vote.application.port.in.DeleteVoteItemUseCase;
import com.example.meet.api.vote.application.port.in.GetVoteUseCase;
import com.example.meet.api.vote.application.port.out.GetVoteItemPort;
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

        if (!user.getRole().equals(MemberRole.admin) && !voteItem.getAuthor().equals(user)) {
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        voteItem.getVote().getVoteItems().remove(voteItem);
        getVoteItemPort.delete(voteItem);

        return DeleteVoteItemResponseDto.builder()
                .deletedId(voteItem.getId().toString())
                .build();
    }
}

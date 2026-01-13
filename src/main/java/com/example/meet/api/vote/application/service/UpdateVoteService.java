package com.example.meet.api.vote.application.service;

import com.example.meet.api.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.PostType;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.api.message.application.port.in.SendMessageUseCase;
import com.example.meet.api.participate.application.port.in.CreateParticipateUseCase;
import com.example.meet.api.post.application.domain.entity.Post;
import com.example.meet.api.post.application.port.in.GetPostUseCase;
import com.example.meet.api.post.application.port.in.UpdatePostUseCase;
import com.example.meet.api.vote.adapter.in.dto.in.TerminateVoteRequestDto;
import com.example.meet.api.vote.adapter.in.dto.in.UpdateVoteRequestDto;
import com.example.meet.api.vote.adapter.in.dto.out.FindVoteResponseDto;
import com.example.meet.api.vote.adapter.in.dto.out.TerminateResponseDto;
import com.example.meet.api.vote.application.domain.entity.Vote;
import com.example.meet.api.vote.application.domain.entity.VoteItem;
import com.example.meet.api.vote.application.port.in.GetVoteUseCase;
import com.example.meet.api.vote.application.port.in.UpdateVoteUseCase;
import com.example.meet.api.vote.application.port.out.GetVoteItemPort;
import com.example.meet.api.vote.application.port.out.UpdateVotePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateVoteService implements UpdateVoteUseCase {

    private final GetLogginedInfoUseCase getLogginedInfoUseCase;
    private final GetVoteUseCase getVoteUseCase;
    private final GetPostUseCase getPostUseCase;
    private final GetVoteItemPort getVoteItemPort;
    private final CreateParticipateUseCase createParticipate;
    private final UpdateVotePort updateVotePort;
    private final UpdatePostUseCase updatePostUseCase;
    private final SendMessageUseCase sendMessageUseCase;

    @Override
    @Transactional
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public FindVoteResponseDto vote(UpdateVoteRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();
        Vote vote = getVoteUseCase.getVote(inDto.getVoteId());

        for (Long voteItemId : inDto.getVoteItemIdList()) {
            getVoteItemPort.get(voteItemId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_NOT_EXISTS));
        }

        updateVotePort.updateVoters(vote.getId(), user, inDto.getVoteItemIdList());

        return getVoteUseCase.getResponseDto(inDto.getVoteId());
    }

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAdminAccess(authentication)")
    public TerminateResponseDto terminate(TerminateVoteRequestDto request) {
        Vote vote = getVoteUseCase.getVote(request.getVoteId());

        String result = null;
        int max = -1;
        int count;

        for (VoteItem voteItem : vote.getVoteItems()) {
            count = 0;

            if (voteItem.getVoters() != null) {
                count = voteItem.getVoters().size();
            }


            if (count > max) {
                max = count;
                result = voteItem.getContent();
            }
        }

        updateVotePort.updateResult(request.getVoteId(), result);

        return TerminateResponseDto.builder()
                .voteId(request.getVoteId())
                .build();
    }

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAdminAccess(authentication)")
    @Transactional
    public void terminateAll(TerminateVoteRequestDto request) {
        Post post = getPostUseCase.getEntity(request.getPostId());

        for(Vote vote : post.getVoteList()) {
            if(!vote.getActiveYn()) continue;

            String result = null;
            int max = -1;
            int count;

            for (VoteItem voteItem : vote.getVoteItems()) {
                count = 0;

                if (voteItem.getVoters() != null) {
                    count = voteItem.getVoters().size();
                }


                if (count > max) {
                    max = count;
                    result = voteItem.getContent();
                }
            }

            updateVotePort.updateResult(vote.getId(), result);
        }

        if(post.getType().equals(PostType.TRAVEL) || post.getType().equals(PostType.MEET)) {
            createParticipate.create(post.getId());
        }

        sendMessageUseCase.sendVoteTerminated(post.getTitle(), post.getId().toString());

        updatePostUseCase.terminateVote(post.getId());
    }
}

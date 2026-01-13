package com.example.meet.participate.application.service;

import com.example.meet.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.member.application.domain.entity.Member;
import com.example.meet.participate.adapter.in.dto.in.TerminateParticipateVoteRequestDto;
import com.example.meet.participate.adapter.in.dto.in.VoteParticipateVoteRequestDto;
import com.example.meet.participate.application.domain.entity.ParticipateVote;
import com.example.meet.participate.application.domain.entity.ParticipateVoteItem;
import com.example.meet.participate.application.port.in.UpdateParticipateVoteUseCase;
import com.example.meet.participate.application.port.out.GetParticipateVotePort;
import com.example.meet.participate.application.port.out.UpdateParticipateVotePort;
import com.example.meet.post.application.domain.entity.Post;
import com.example.meet.post.application.port.in.GetPostUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateParticipateVoteService implements UpdateParticipateVoteUseCase {
    private final GetLogginedInfoUseCase getLogginedInfoUseCase;
    private final GetParticipateVotePort getParticipateVotePort;
    private final UpdateParticipateVotePort updateParticipateVotePort;

    private final GetPostUseCase getPostUseCase;

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAdminAccess(authentication)")
    public void terminate(TerminateParticipateVoteRequestDto inDto) {
        Post post = getPostUseCase.getEntity(inDto.getPostId());
        ParticipateVote vote = post.getParticipateVote();

        Optional<ParticipateVoteItem> item = vote.getParticipateVoteItems().stream().filter((ParticipateVoteItem::getIsParticipate)).findFirst();

        List<Member> participants = new ArrayList<>();

        if(item.isPresent()) {
            participants = item.get().getParticipateVoters();
        }

        updateParticipateVotePort.terminate(vote.getId(), participants.size());

    }

    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    @Override
    public void vote(VoteParticipateVoteRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();

        updateParticipateVotePort.updateVoters(inDto.getParticipateVoteItemId(), user);

    }
}

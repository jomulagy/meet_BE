package com.example.meet.api.participate.application.service;

import com.example.meet.batch.application.port.in.RegisterJobUseCase;
import com.example.meet.api.participate.application.domain.entity.ParticipateVote;
import com.example.meet.api.participate.application.domain.entity.ParticipateVoteItem;
import com.example.meet.api.participate.application.port.in.CreateParticipateUseCase;
import com.example.meet.api.participate.application.port.out.CreateParticipateVoteItemPort;
import com.example.meet.api.participate.application.port.out.CreateParticipateVotePort;
import com.example.meet.api.post.application.domain.entity.Post;
import com.example.meet.api.post.application.port.in.GetPostUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CreateParticipateService implements CreateParticipateUseCase {
    private final GetPostUseCase getPostUseCase;
    private final CreateParticipateVotePort createParticipateVotePort;
    private final CreateParticipateVoteItemPort createParticipateVoteItemPort;
    private final RegisterJobUseCase registerJobUseCase;

    @Override
    @Transactional
    @PreAuthorize("@memberPermissionEvaluator.hasAdminAccess(authentication)")
    public void create(Long postId) {
        Post post = getPostUseCase.getEntity(postId);

        ParticipateVote participateVote = ParticipateVote.builder()
                .totalNum(0)
                .endDate(LocalDate.now().plusDays(4).atTime(0,0))
                .isActive(true)
                .post(post)
                .build();

        ParticipateVote saved = createParticipateVotePort.create(participateVote);

        addVoteItems(saved);

        registerJobUseCase.terminateParticipateVote(saved);
    }

    private void addVoteItems(ParticipateVote saved) {

        createParticipateVoteItemPort.create(
                ParticipateVoteItem.builder()
                        .isParticipate(true)
                        .participateVote(saved)
                        .build()
        );

        createParticipateVoteItemPort.create(
                ParticipateVoteItem.builder()
                        .isParticipate(false)
                        .participateVote(saved)
                        .build()
        );

    }
}

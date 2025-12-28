package com.example.meet.participate.application.service;

import com.example.meet.participate.application.port.in.DeleteParticipateUseCase;
import com.example.meet.participate.application.port.out.DeleteParticipatePort;
import com.example.meet.post.application.domain.entity.Post;
import com.example.meet.post.application.port.in.GetPostUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteParticipateService implements DeleteParticipateUseCase {
    private final GetPostUseCase getPostUseCase;
    private final DeleteParticipatePort deleteParticipatePort;

    @Override
    public void delete(Long postId) {
        Post post = getPostUseCase.getEntity(postId);

        deleteParticipatePort.delete(post.getParticipateVote().getId());
    }
}

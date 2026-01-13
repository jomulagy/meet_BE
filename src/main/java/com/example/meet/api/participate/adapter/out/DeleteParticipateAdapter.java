package com.example.meet.api.participate.adapter.out;

import com.example.meet.infrastructure.repository.ParticipateVoteRepository;
import com.example.meet.api.participate.application.port.out.DeleteParticipatePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeleteParticipateAdapter implements DeleteParticipatePort {
    private final ParticipateVoteRepository repository;

    @Override
    public void delete(Long voteId) {
        repository.deleteById(voteId);
    }
}

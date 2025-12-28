package com.example.meet.participate.adapter.out;

import com.example.meet.infrastructure.repository.ParticipateVoteRepository;
import com.example.meet.participate.application.domain.entity.ParticipateVote;
import com.example.meet.participate.application.port.out.GetParticipateVotePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GetParticipateVoteAdapter implements GetParticipateVotePort {
    private final ParticipateVoteRepository repository;

    @Override
    public Optional<ParticipateVote> get(Long voteId) {
        return repository.findById(voteId);
    }
}

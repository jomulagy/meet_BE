package com.example.meet.participate.adapter.out;

import com.example.meet.infrastructure.repository.ParticipateVoteRepository;
import com.example.meet.participate.application.domain.entity.ParticipateVote;
import com.example.meet.participate.application.port.out.CreateParticipateVotePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CreateParticipateVoteAdapter implements CreateParticipateVotePort {
    private final ParticipateVoteRepository participateVoteRepository;

    @Override
    public ParticipateVote create(ParticipateVote participateVote) {
        return participateVoteRepository.save(participateVote);
    }
}

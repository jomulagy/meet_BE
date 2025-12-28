package com.example.meet.vote.adapter.out;

import com.example.meet.infrastructure.repository.VoteRepository;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.port.out.CreateVotePort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CreateVoteAdapter implements CreateVotePort {
    private final VoteRepository voteRepository;

    @Override
    @Transactional
    public Vote create(Vote vote) {
        return voteRepository.save(vote);
    }
}

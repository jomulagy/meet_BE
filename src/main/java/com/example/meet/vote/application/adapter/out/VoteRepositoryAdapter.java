package com.example.meet.vote.application.adapter.out;

import com.example.meet.vote.application.adapter.out.jpa.VoteJpaRepository;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.port.out.VoteRepositoryPort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VoteRepositoryAdapter implements VoteRepositoryPort {
    private final VoteJpaRepository voteJpaRepository;

    @Override
    public Optional<Vote> findById(Long id) {
        return voteJpaRepository.findById(id);
    }

    @Override
    public Optional<Vote> findByMeetId(Long meetId) {
        return voteJpaRepository.findByMeetId(meetId);
    }

    @Override
    public Vote save(Vote vote) {
        return voteJpaRepository.save(vote);
    }
}

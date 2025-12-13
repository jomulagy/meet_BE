package com.example.meet.vote.adapter.out;

import com.example.meet.infrastructure.repository.VoteRepository;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.port.out.GetVotePort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GetVoteAdapter implements GetVotePort {
    private final VoteRepository voteRepository;

    @Override
    public Optional<Vote> get(Long id) {
        return voteRepository.findById(id);
    }

    @Override
    public Optional<Vote> getByMeetId(Long meetId) {
        return voteRepository.findByMeetId(meetId);
    }
}

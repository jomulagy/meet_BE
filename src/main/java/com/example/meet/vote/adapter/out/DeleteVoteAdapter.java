package com.example.meet.vote.adapter.out;

import com.example.meet.infrastructure.repository.VoteRepository;
import com.example.meet.vote.application.port.out.DeleteVotePort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeleteVoteAdapter implements DeleteVotePort {
    private final VoteRepository voteRepository;

    @Override
    @Transactional
    public void delete(Long voteItemId) {
        voteRepository.deleteById(voteItemId);
    }
}

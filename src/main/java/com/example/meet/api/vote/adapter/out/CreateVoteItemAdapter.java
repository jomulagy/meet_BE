package com.example.meet.api.vote.adapter.out;

import com.example.meet.infrastructure.repository.VoteItemRepository;
import com.example.meet.infrastructure.repository.VoteRepository;
import com.example.meet.api.vote.application.domain.entity.Vote;
import com.example.meet.api.vote.application.domain.entity.VoteItem;
import com.example.meet.api.vote.application.port.out.CreateVoteItemPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CreateVoteItemAdapter implements CreateVoteItemPort {
    private final VoteRepository voteRepository;
    private final VoteItemRepository voteItemRepository;

    @Override
    @Transactional
    public VoteItem create(VoteItem voteItem) {
        VoteItem saved = voteItemRepository.save(voteItem);

        Vote vote = saved.getVote();
        vote.getVoteItems().add(saved);

        voteRepository.save(vote);
        return saved;
    }
}

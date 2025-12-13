package com.example.meet.vote.adapter.out;

import com.example.meet.infrastructure.repository.VoteItemRepository;
import com.example.meet.infrastructure.repository.VoteRepository;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.domain.entity.VoteItem;
import com.example.meet.vote.application.port.out.CreatVoteItemPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CreatVoteItemAdapter implements CreatVoteItemPort {
    private final VoteRepository voteRepository;
    private final VoteItemRepository voteItemRepository;

    @Override
    @Transactional
    public void create(VoteItem voteItem) {
        voteItemRepository.save(voteItem);

        Vote vote = voteItem.getVote();
        vote.getVoteItems().add(voteItem);

        voteRepository.save(vote);
    }
}

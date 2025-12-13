package com.example.meet.vote.application.adapter.out;

import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.domain.entity.VoteItem;
import com.example.meet.vote.application.port.out.VoteRepositoryPort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VoteRepositoryAdapter implements VoteRepositoryPort {

    private final VoteJpaRepository voteJpaRepository;
    private final VoteItemJpaRepository voteItemJpaRepository;

    @Override
    public Optional<Vote> findByMeetId(Long meetId) {
        return voteJpaRepository.findByMeetId(meetId);
    }

    @Override
    public Vote saveVote(Vote vote) {
        return voteJpaRepository.save(vote);
    }

    @Override
    public VoteItem saveVoteItem(VoteItem voteItem) {
        return voteItemJpaRepository.save(voteItem);
    }

    @Override
    public Optional<VoteItem> findVoteItemById(Long voteItemId) {
        return voteItemJpaRepository.findById(voteItemId);
    }

    @Override
    public void deleteVoteItem(VoteItem voteItem) {
        voteItemJpaRepository.delete(voteItem);
    }

    @Override
    public List<VoteItem> findVoteItemsById(List<Long> voteItemIds) {
        return voteItemJpaRepository.findAllById(voteItemIds);
    }
}

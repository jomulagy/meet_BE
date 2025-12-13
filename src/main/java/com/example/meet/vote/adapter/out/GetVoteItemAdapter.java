package com.example.meet.vote.adapter.out;

import com.example.meet.infrastructure.repository.VoteItemRepository;
import com.example.meet.vote.application.domain.entity.VoteItem;
import com.example.meet.vote.application.port.out.GetVoteItemPort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GetVoteItemAdapter implements GetVoteItemPort {
    private final VoteItemRepository voteItemRepository;

    @Override
    public Optional<VoteItem> get(Long id) {
        return voteItemRepository.findById(id);
    }

    @Override
    public VoteItem save(VoteItem voteItem) {
        return voteItemRepository.save(voteItem);
    }

    @Override
    public void delete(VoteItem voteItem) {
        voteItemRepository.delete(voteItem);
    }
}

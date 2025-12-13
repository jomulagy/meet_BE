package com.example.meet.vote.application.adapter.out;

import com.example.meet.vote.application.adapter.out.jpa.VoteItemJpaRepository;
import com.example.meet.vote.application.domain.entity.VoteItem;
import com.example.meet.vote.application.port.out.VoteItemRepositoryPort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VoteItemRepositoryAdapter implements VoteItemRepositoryPort {
    private final VoteItemJpaRepository voteItemJpaRepository;

    @Override
    public Optional<VoteItem> findById(Long id) {
        return voteItemJpaRepository.findById(id);
    }

    @Override
    public VoteItem save(VoteItem voteItem) {
        return voteItemJpaRepository.save(voteItem);
    }

    @Override
    public void delete(VoteItem voteItem) {
        voteItemJpaRepository.delete(voteItem);
    }
}

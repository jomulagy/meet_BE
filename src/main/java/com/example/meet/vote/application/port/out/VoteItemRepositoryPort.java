package com.example.meet.vote.application.port.out;

import com.example.meet.vote.application.domain.entity.VoteItem;
import java.util.Optional;

public interface VoteItemRepositoryPort {
    Optional<VoteItem> findById(Long id);

    VoteItem save(VoteItem voteItem);

    void delete(VoteItem voteItem);
}

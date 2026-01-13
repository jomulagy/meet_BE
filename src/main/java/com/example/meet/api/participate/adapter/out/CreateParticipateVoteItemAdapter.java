package com.example.meet.api.participate.adapter.out;

import com.example.meet.infrastructure.repository.ParticipateVoteItemRepository;
import com.example.meet.api.participate.application.domain.entity.ParticipateVoteItem;
import com.example.meet.api.participate.application.port.out.CreateParticipateVoteItemPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CreateParticipateVoteItemAdapter implements CreateParticipateVoteItemPort {
    private final ParticipateVoteItemRepository repository;

    @Override
    public ParticipateVoteItem create(ParticipateVoteItem item) {
        return repository.save(item);
    }
}

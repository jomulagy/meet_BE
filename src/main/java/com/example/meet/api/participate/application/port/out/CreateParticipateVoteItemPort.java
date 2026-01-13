package com.example.meet.api.participate.application.port.out;

import com.example.meet.api.participate.application.domain.entity.ParticipateVoteItem;

public interface CreateParticipateVoteItemPort {
    ParticipateVoteItem create(ParticipateVoteItem build);
}

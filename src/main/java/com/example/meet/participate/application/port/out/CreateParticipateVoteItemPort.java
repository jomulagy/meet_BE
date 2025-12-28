package com.example.meet.participate.application.port.out;

import com.example.meet.participate.application.domain.entity.ParticipateVoteItem;

public interface CreateParticipateVoteItemPort {
    ParticipateVoteItem create(ParticipateVoteItem build);
}

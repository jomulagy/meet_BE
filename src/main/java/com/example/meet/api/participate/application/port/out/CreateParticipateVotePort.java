package com.example.meet.api.participate.application.port.out;

import com.example.meet.api.participate.application.domain.entity.ParticipateVote;

public interface CreateParticipateVotePort {
    ParticipateVote create(ParticipateVote participateVote);
}

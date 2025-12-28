package com.example.meet.participate.application.port.out;

import com.example.meet.participate.application.domain.entity.ParticipateVote;

public interface CreateParticipateVotePort {
    ParticipateVote create(ParticipateVote participateVote);
}

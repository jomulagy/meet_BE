package com.example.meet.participate.application.port.out;

import com.example.meet.participate.application.domain.entity.ParticipateVote;

import java.util.Optional;

public interface GetParticipateVotePort  {
    Optional<ParticipateVote> get(Long voteId);
}

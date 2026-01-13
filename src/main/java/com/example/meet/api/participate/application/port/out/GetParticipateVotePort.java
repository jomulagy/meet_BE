package com.example.meet.api.participate.application.port.out;

import com.example.meet.api.participate.application.domain.entity.ParticipateVote;

import java.util.Optional;

public interface GetParticipateVotePort  {
    Optional<ParticipateVote> get(Long voteId);
}

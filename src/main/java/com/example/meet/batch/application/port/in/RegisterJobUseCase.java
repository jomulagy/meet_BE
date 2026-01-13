package com.example.meet.batch.application.port.in;

import com.example.meet.api.participate.application.domain.entity.ParticipateVote;
import com.example.meet.api.vote.application.domain.entity.Vote;

public interface RegisterJobUseCase {
    void terminateVote(Vote savedVote);

    void terminateParticipateVote(ParticipateVote participateVote);
}

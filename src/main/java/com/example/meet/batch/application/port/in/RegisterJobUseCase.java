package com.example.meet.batch.application.port.in;

import com.example.meet.participate.application.domain.entity.ParticipateVote;
import com.example.meet.vote.application.domain.entity.Vote;

public interface RegisterJobUseCase {
    void terminateVote(Vote savedVote);

    void terminateParticipateVote(ParticipateVote participateVote);
}

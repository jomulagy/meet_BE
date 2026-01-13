package com.example.meet.api.vote.application.port.out;

import com.example.meet.api.vote.application.domain.entity.Vote;

public interface CreateVotePort {
    Vote create(Vote vote);
}

package com.example.meet.vote.application.port.out;

import com.example.meet.vote.application.domain.entity.Vote;

public interface CreateVotePort {
    Vote create(Vote vote);
}

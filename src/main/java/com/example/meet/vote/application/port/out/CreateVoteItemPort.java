package com.example.meet.vote.application.port.out;

import com.example.meet.vote.application.domain.entity.VoteItem;

public interface CreateVoteItemPort {
    VoteItem create(VoteItem voteItem);
}

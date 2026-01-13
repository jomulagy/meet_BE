package com.example.meet.api.vote.application.port.out;

import com.example.meet.api.vote.application.domain.entity.VoteItem;

public interface CreateVoteItemPort {
    VoteItem create(VoteItem voteItem);
}

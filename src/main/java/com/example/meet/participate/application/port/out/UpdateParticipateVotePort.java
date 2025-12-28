package com.example.meet.participate.application.port.out;

import com.example.meet.entity.Member;

public interface UpdateParticipateVotePort {
    void terminate(Long voteId, int participants);

    void updateVoters(Long participateVoteItemId, Member user);
}

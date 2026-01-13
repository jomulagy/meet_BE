package com.example.meet.api.participate.application.port.out;

import com.example.meet.api.member.application.domain.entity.Member;

public interface UpdateParticipateVotePort {
    void terminate(Long voteId, int participants);

    void updateVoters(Long participateVoteItemId, Member user);
}

package com.example.meet.vote.application.port.out;

import com.example.meet.member.application.domain.entity.Member;

import java.util.List;

public interface UpdateVotePort {
    void updateVoters(Long voteId, Member voter, List<Long> votedIdList);

    void updateResult(Long id, String contentResult);
}

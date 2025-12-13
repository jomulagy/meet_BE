package com.example.meet.vote.application.port.out;

import com.example.meet.entity.Member;
import java.util.List;

public interface UpdateVotePort {
    void updateVoteItems(Long voteId, Member voter, List<UpdateVoteItemCommand> commands);

    record UpdateVoteItemCommand(Long voteItemId, Boolean isVote) {
    }
}

package com.example.meet.vote.application.port.out;

import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.domain.entity.VoteItem;
import java.util.List;
import java.util.Optional;

public interface VoteRepositoryPort {
    Optional<Vote> findByMeetId(Long meetId);

    Vote saveVote(Vote vote);

    VoteItem saveVoteItem(VoteItem voteItem);

    Optional<VoteItem> findVoteItemById(Long voteItemId);

    void deleteVoteItem(VoteItem voteItem);

    List<VoteItem> findVoteItemsById(List<Long> voteItemIds);
}

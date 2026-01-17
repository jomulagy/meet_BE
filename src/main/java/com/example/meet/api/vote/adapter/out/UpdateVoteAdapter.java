package com.example.meet.api.vote.adapter.out;

import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.infrastructure.repository.VoteItemRepository;
import com.example.meet.api.vote.application.domain.entity.VoteItem;
import com.example.meet.api.vote.application.port.out.UpdateVotePort;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.meet.api.vote.application.domain.entity.QVote.vote;
import static com.example.meet.api.vote.application.domain.entity.QVoteItem.voteItem;

@Repository
@RequiredArgsConstructor
public class UpdateVoteAdapter implements UpdateVotePort {
    private final JPAQueryFactory queryFactory;
    private final VoteItemRepository voteItemRepository;

    @Override
    public void updateVoters(Long voteId, Member voter, List<Long> votedIdList) {
        List<VoteItem> voteItems = queryFactory.selectFrom(voteItem)
                .leftJoin(voteItem.voters).fetchJoin()
                .where(voteItem.vote.id.eq(voteId))
                .fetch();

        for (VoteItem item : voteItems) {
            boolean containsUser = item.getVoters().contains(voter);
            boolean shouldContain = votedIdList.contains(item.getId());

            if (containsUser && !shouldContain) {
                item.getVoters().removeIf(member -> member.equals(voter));
            } else if (!containsUser && shouldContain) {
                item.getVoters().add(voter);
            }
        }

        voteItemRepository.saveAll(voteItems);
    }

    @Override
    @Transactional
    public void updateResult(Long id, VoteItem result) {
        queryFactory
                .update(vote)
                .set(vote.result, result)
                .set(vote.activeYn, false)
                .where(vote.id.eq(id))
                .execute();
    }
}

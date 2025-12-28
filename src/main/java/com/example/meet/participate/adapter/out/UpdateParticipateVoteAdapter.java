package com.example.meet.participate.adapter.out;

import com.example.meet.entity.Member;
import com.example.meet.infrastructure.repository.ParticipateVoteItemRepository;
import com.example.meet.participate.application.domain.entity.ParticipateVote;
import com.example.meet.participate.application.domain.entity.ParticipateVoteItem;
import com.example.meet.participate.application.port.out.UpdateParticipateVotePort;
import com.example.meet.vote.application.domain.entity.VoteItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.meet.participate.application.domain.entity.QParticipateVote.participateVote;
import static com.example.meet.participate.application.domain.entity.QParticipateVoteItem.participateVoteItem;
import static com.example.meet.vote.application.domain.entity.QVoteItem.voteItem;

@Repository
@RequiredArgsConstructor
public class UpdateParticipateVoteAdapter implements UpdateParticipateVotePort {
    private final JPAQueryFactory queryFactory;
    private final ParticipateVoteItemRepository participateVoteItemRepository;

    @Override
    @Transactional
    public void terminate(Long id, int participants) {
        queryFactory
                .update(participateVote)
                .set(participateVote.totalNum, Long.valueOf(participants))
                .set(participateVote.isActive, false)
                .where(participateVote.id.eq(id))
                .execute();
    }

    @Override
    public void updateVoters(Long participateVoteItemId, Member user) {
        ParticipateVoteItem item = queryFactory.selectFrom(participateVoteItem)
                .leftJoin(participateVoteItem.participateVoters).fetchJoin()
                .where(participateVoteItem.id.eq(participateVoteItemId))
                .fetchFirst();

        ParticipateVote participateVote = item.getParticipateVote();

        for(ParticipateVoteItem participateVoteItem : participateVote.getParticipateVoteItems()){
            participateVoteItem.getParticipateVoters().removeIf(member -> member.equals(user));
        }

        item.getParticipateVoters().add(user);

        participateVoteItemRepository.save(item);
    }
}

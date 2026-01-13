package com.example.meet.api.participate.adapter.out;

import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.infrastructure.repository.ParticipateVoteItemRepository;
import com.example.meet.api.participate.application.domain.entity.ParticipateVote;
import com.example.meet.api.participate.application.domain.entity.ParticipateVoteItem;
import com.example.meet.api.participate.application.port.out.UpdateParticipateVotePort;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.example.meet.api.participate.application.domain.entity.QParticipateVote.participateVote;
import static com.example.meet.api.participate.application.domain.entity.QParticipateVoteItem.participateVoteItem;

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

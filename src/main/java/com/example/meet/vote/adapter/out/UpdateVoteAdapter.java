package com.example.meet.vote.adapter.out;

import com.example.meet.entity.Member;
import com.example.meet.vote.adapter.out.jpa.VoteItemJpaRepository;
import com.example.meet.vote.application.domain.entity.VoteItem;
import com.example.meet.vote.application.port.out.UpdateVotePort;
import com.example.meet.vote.application.port.out.UpdateVotePort.UpdateVoteItemCommand;
import com.example.meet.vote.application.domain.entity.QVoteItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UpdateVoteAdapter implements UpdateVotePort {
    private final JPAQueryFactory queryFactory;
    private final VoteItemJpaRepository voteItemJpaRepository;

    @Override
    public void updateVoteItems(Long voteId, Member voter, List<UpdateVoteItemCommand> commands) {
        QVoteItem voteItem = QVoteItem.voteItem;
        List<VoteItem> voteItems = queryFactory.selectFrom(voteItem)
                .leftJoin(voteItem.voters).fetchJoin()
                .where(voteItem.vote.id.eq(voteId))
                .fetch();

        Map<Long, VoteItem> voteItemMap = voteItems.stream()
                .collect(Collectors.toMap(VoteItem::getId, Function.identity()));

        for (UpdateVoteItemCommand command : commands) {
            VoteItem item = voteItemMap.get(command.voteItemId());
            if (item == null) {
                continue;
            }

            boolean containsUser = item.getVoters().contains(voter);
            boolean shouldContain = Boolean.TRUE.equals(command.isVote());

            if (containsUser && !shouldContain) {
                item.getVoters().removeIf(member -> member.equals(voter));
            } else if (!containsUser && shouldContain) {
                item.getVoters().add(voter);
            }
        }

        voteItemJpaRepository.saveAll(voteItems);
    }
}

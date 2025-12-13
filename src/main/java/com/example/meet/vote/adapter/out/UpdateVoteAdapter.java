package com.example.meet.vote.adapter.out;

import com.example.meet.entity.Member;
import com.example.meet.vote.adapter.in.dto.in.UpdateVoteItemRequestDto;
import com.example.meet.infrastructure.repository.VoteItemRepository;
import com.example.meet.vote.application.domain.entity.VoteItem;
import com.example.meet.vote.application.port.out.UpdateVotePort;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.meet.vote.application.domain.entity.QVoteItem.voteItem;

@Repository
@RequiredArgsConstructor
public class UpdateVoteAdapter implements UpdateVotePort {
    private final JPAQueryFactory queryFactory;
    private final VoteItemRepository voteItemRepository;

    @Override
    public void updateVoters(Long voteId, Member voter, List<UpdateVoteItemRequestDto> dtoList) {
        List<VoteItem> voteItems = queryFactory.selectFrom(voteItem)
                .leftJoin(voteItem.voters).fetchJoin()
                .where(voteItem.vote.id.eq(voteId))
                .fetch();

        Map<Long, VoteItem> voteItemMap = voteItems.stream()
                .collect(Collectors.toMap(VoteItem::getId, Function.identity()));

        for (UpdateVoteItemRequestDto dto : dtoList) {
            VoteItem item = voteItemMap.get(dto.getVoteItemId());
            if (item == null) {
                continue;
            }

            boolean containsUser = item.getVoters().contains(voter);
            boolean shouldContain = dto.isVote();

            if (containsUser && !shouldContain) {
                item.getVoters().removeIf(member -> member.equals(voter));
            } else if (!containsUser && shouldContain) {
                item.getVoters().add(voter);
            }
        }

        voteItemRepository.saveAll(voteItems);
    }
}

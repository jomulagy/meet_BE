package com.example.meet.api.vote.adapter.out;

import com.example.meet.infrastructure.repository.VoteRepository;
import com.example.meet.api.vote.application.domain.entity.Vote;
import com.example.meet.api.vote.application.port.out.GetVotePort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.example.meet.api.vote.application.domain.entity.QVote.vote;


@Repository
@RequiredArgsConstructor
public class GetVoteAdapter implements GetVotePort {
    private final JPAQueryFactory query;
    private final VoteRepository voteRepository;

    @Override
    public Optional<Vote> get(Long id) {
        return voteRepository.findById(id);
    }

    @Override
    public List<Vote> getByPostId(Long postId) {
        return voteRepository.findByPostId(postId);
    }

    @Override
    public List<Vote> getListByEndDate(LocalDateTime dateTime) {
        return query
                .selectFrom(vote)
                .where(
                        vote.endDate.loe(dateTime),
                        vote.activeYn.eq(true)
                )
                .fetch();
    }
}

package com.example.meet.api.attendance.adapter.out;

import com.example.meet.api.attendance.application.domain.entity.Penalty;
import com.example.meet.api.attendance.application.domain.entity.QPenalty;
import com.example.meet.api.attendance.application.port.out.GetPenaltyPort;
import com.example.meet.infrastructure.repository.PenaltyRepository;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GetPenaltyAdapter implements GetPenaltyPort {
    private final PenaltyRepository penaltyRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Penalty> findLatestByMemberId(Long memberId) {
        QPenalty p = QPenalty.penalty;
        QPenalty sub = new QPenalty("sub");

        return Optional.ofNullable(
                queryFactory.selectFrom(p)
                        .where(p.member.id.eq(memberId)
                                .and(p.id.eq(
                                        JPAExpressions.select(sub.id.max())
                                                .from(sub)
                                                .where(sub.member.id.eq(memberId))
                                )))
                        .fetchOne()
        );
    }

    @Override
    public Optional<Penalty> findByMemberIdAndPostId(Long memberId, Long postId) {
        return penaltyRepository.findByMemberIdAndPostId(memberId, postId);
    }

    @Override
    public List<Penalty> findLatestForAllMembers() {
        QPenalty p = QPenalty.penalty;
        QPenalty sub = new QPenalty("sub");

        List<Long> maxIds = queryFactory
                .select(sub.id.max())
                .from(sub)
                .groupBy(sub.member.id)
                .fetch();

        return queryFactory
                .selectFrom(p)
                .where(p.id.in(maxIds))
                .fetch();
    }
}

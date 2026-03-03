package com.example.meet.api.attendance.application.port.out;

import com.example.meet.api.attendance.application.domain.entity.Penalty;

import java.util.List;
import java.util.Optional;

public interface GetPenaltyPort {
    Optional<Penalty> findLatestByMemberId(Long memberId);
    Optional<Penalty> findByMemberIdAndPostId(Long memberId, Long postId);
    List<Penalty> findLatestForAllMembers();
}

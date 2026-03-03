package com.example.meet.infrastructure.repository;

import com.example.meet.api.attendance.application.domain.entity.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {
    Optional<Penalty> findByMemberIdAndPostId(Long memberId, Long postId);
}

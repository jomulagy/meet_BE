package com.example.meet.infrastructure.repository;

import com.example.meet.vote.application.domain.entity.Vote;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByMeetId(Long meetId);
}

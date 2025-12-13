package com.example.meet.vote.application.adapter.out;

import com.example.meet.vote.application.domain.entity.Vote;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteJpaRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByMeetId(Long meetId);
}

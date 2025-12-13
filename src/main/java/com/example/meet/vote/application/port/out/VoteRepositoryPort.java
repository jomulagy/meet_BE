package com.example.meet.vote.application.port.out;

import com.example.meet.vote.application.domain.entity.Vote;
import java.util.Optional;

public interface VoteRepositoryPort {
    Optional<Vote> findById(Long id);

    Optional<Vote> findByMeetId(Long meetId);

    Vote save(Vote vote);
}

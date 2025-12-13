package com.example.meet.vote.application.port.out;

import com.example.meet.vote.application.domain.entity.Vote;
import java.util.Optional;

public interface GetVotePort {
    Optional<Vote> get(Long id);

    Optional<Vote> getByMeetId(Long meetId);
}

package com.example.meet.api.vote.application.port.out;

import com.example.meet.api.vote.application.domain.entity.Vote;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GetVotePort {
    Optional<Vote> get(Long id);

    List<Vote> getByPostId(Long postId);

    List<Vote> getListByEndDate(LocalDateTime dateTime);
}

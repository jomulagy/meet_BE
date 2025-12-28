package com.example.meet.infrastructure.repository;

import com.example.meet.vote.application.domain.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findByPostId(Long postId);
}

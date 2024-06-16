package com.example.meet.repository;

import com.example.meet.entity.ParticipateVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipateVoteRepository extends JpaRepository<ParticipateVote, Long> {

}

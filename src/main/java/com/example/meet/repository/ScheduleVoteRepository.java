package com.example.meet.repository;

import com.example.meet.entity.ScheduleVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleVoteRepository extends JpaRepository<ScheduleVote, Long> {

}

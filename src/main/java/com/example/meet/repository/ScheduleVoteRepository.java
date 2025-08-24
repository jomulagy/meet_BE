package com.example.meet.repository;

import com.example.meet.entity.ScheduleVote;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScheduleVoteRepository extends JpaRepository<ScheduleVote, Long> {
}

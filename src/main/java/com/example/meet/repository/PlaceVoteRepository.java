package com.example.meet.repository;

import com.example.meet.entity.PlaceVote;
import com.example.meet.entity.ScheduleVote;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceVoteRepository extends JpaRepository<PlaceVote, Long> {
}

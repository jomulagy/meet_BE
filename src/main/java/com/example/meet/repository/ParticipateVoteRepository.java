package com.example.meet.repository;

import com.example.meet.entity.ParticipateVote;
import com.example.meet.entity.PlaceVote;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipateVoteRepository extends JpaRepository<ParticipateVote, Long> {

    @Query("SELECT e FROM ParticipateVote e WHERE FUNCTION('DATE', e.endDate) = CURRENT_DATE")
    List<ParticipateVote> findByEndDateToday();
}

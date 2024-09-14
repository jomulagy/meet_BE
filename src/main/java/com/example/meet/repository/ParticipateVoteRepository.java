package com.example.meet.repository;

import com.example.meet.entity.ParticipateVote;
import com.example.meet.entity.PlaceVote;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipateVoteRepository extends JpaRepository<ParticipateVote, Long> {

    List<ParticipateVote> findByEndDateBeforeAndTotalNumIsNull(LocalDateTime currentDate);
}

package com.example.meet.repository;

import com.example.meet.meet.domain.entity.Meet;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetRepository extends JpaRepository<Meet, Long> {

    @EntityGraph(attributePaths = {"participants"})
    List<Meet> findByDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<Meet> findByDateIsNotNullAndPlaceIsNotNullAndParticipantsNumIsNull();

}

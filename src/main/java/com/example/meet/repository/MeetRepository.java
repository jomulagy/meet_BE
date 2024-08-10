package com.example.meet.repository;

import com.example.meet.entity.Meet;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetRepository extends JpaRepository<Meet, Long> {

    List<Meet> findByDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}

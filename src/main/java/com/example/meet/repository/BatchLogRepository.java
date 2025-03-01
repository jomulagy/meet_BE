package com.example.meet.repository;

import com.example.meet.entity.BatchLog;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchLogRepository extends JpaRepository<BatchLog, Long> {
    List<BatchLog> findByStatusAndStartDateAfter(String status, LocalDateTime startDate);
}

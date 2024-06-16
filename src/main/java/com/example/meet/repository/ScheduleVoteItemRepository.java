package com.example.meet.repository;

import com.example.meet.entity.ScheduleVoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleVoteItemRepository extends JpaRepository<ScheduleVoteItem, Long> {

}

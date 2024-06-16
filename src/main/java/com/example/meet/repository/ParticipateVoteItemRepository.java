package com.example.meet.repository;

import com.example.meet.entity.ParticipateVoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipateVoteItemRepository extends JpaRepository<ParticipateVoteItem, Long> {

}

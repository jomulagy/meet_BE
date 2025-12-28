package com.example.meet.infrastructure.repository;

import com.example.meet.participate.application.domain.entity.ParticipateVoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipateVoteItemRepository extends JpaRepository<ParticipateVoteItem, Long> {

}

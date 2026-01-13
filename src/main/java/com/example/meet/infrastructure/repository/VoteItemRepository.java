package com.example.meet.infrastructure.repository;

import com.example.meet.api.vote.application.domain.entity.VoteItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteItemRepository extends JpaRepository<VoteItem, Long> {
}

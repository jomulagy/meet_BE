package com.example.meet.vote.adapter.out.jpa;

import com.example.meet.vote.application.domain.entity.VoteItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteItemJpaRepository extends JpaRepository<VoteItem, Long> {
}

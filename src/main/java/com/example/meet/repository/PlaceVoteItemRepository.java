package com.example.meet.repository;

import com.example.meet.entity.PlaceVoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceVoteItemRepository extends JpaRepository<PlaceVoteItem, Long> {

}

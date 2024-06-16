package com.example.meet.repository;

import com.example.meet.entity.PlaceVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceVoteRepository extends JpaRepository<PlaceVote, Long> {

}

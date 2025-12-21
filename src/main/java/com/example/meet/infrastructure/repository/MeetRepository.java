package com.example.meet.infrastructure.repository;

import com.example.meet.post.application.domain.entity.Post;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"participants"})
    List<Post> findByDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<Post> findByDateIsNotNullAndPlaceIsNotNullAndParticipantsNumIsNull();

}

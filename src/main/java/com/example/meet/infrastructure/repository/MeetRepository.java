package com.example.meet.infrastructure.repository;

import com.example.meet.api.post.application.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetRepository extends JpaRepository<Post, Long> {
}

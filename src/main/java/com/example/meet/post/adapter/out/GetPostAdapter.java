package com.example.meet.post.adapter.out;

import com.example.meet.infrastructure.repository.MeetRepository;
import com.example.meet.post.application.domain.entity.Post;
import com.example.meet.post.application.port.out.GetPostPort;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.meet.post.application.domain.entity.QPost.post;

@Repository
@RequiredArgsConstructor
public class GetPostAdapter implements GetPostPort {
    private final JPAQueryFactory query;
    private final MeetRepository meetRepository;

    @Override
    public Optional<Post> getPostById(Long meetId) {
        return meetRepository.findById(meetId);
    }

    @Override
    public List<Post> findAll() {
        return query
                .selectFrom(post)
                .orderBy(post.id.desc())
                .fetch();
    }
}

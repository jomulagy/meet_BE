package com.example.meet.post.adapter.out;

import com.example.meet.infrastructure.repository.PostRepository;
import com.example.meet.post.application.domain.entity.Post;
import com.example.meet.post.application.port.out.CreatePostPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CreatePostAdapter implements CreatePostPort {
    private final PostRepository postRepository;

    @Override
    @Transactional
    public void create(Post post) {
        postRepository.save(post);
    }
}

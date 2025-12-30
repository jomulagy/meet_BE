package com.example.meet.post.application.port.out;

import com.example.meet.infrastructure.enumulation.PostType;
import com.example.meet.post.application.domain.entity.Post;

import java.util.List;
import java.util.Optional;

public interface GetPostPort {
    Optional<Post> getPostById(Long meetId);

    List<Post> findAll();

    List<Post> findListByType(PostType type);
}

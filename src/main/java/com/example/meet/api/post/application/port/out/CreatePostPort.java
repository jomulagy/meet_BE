package com.example.meet.api.post.application.port.out;

import com.example.meet.api.post.application.domain.entity.Post;

public interface CreatePostPort {
    Post create(Post post);
}

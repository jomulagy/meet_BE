package com.example.meet.post.application.port.out;

import com.example.meet.post.application.domain.entity.Post;

public interface CreatePostPort {
    Post create(Post post);
}

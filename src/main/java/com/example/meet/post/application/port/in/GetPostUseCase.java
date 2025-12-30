package com.example.meet.post.application.port.in;

import com.example.meet.post.adapter.in.dto.out.GetPostResponseDto;
import com.example.meet.post.adapter.in.dto.in.GetPostRequestDto;
import com.example.meet.post.application.domain.entity.Post;

import java.util.List;

public interface GetPostUseCase {
    GetPostResponseDto get(GetPostRequestDto inDto);

    List<GetPostResponseDto> findPostList(String type);

    Post getEntity(Long meetId);
}

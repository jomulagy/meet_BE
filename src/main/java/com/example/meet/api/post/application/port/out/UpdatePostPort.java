package com.example.meet.api.post.application.port.out;

import com.example.meet.api.post.adapter.in.dto.in.UpdatePostRequestDto;

public interface UpdatePostPort {
    long update(UpdatePostRequestDto inDto);

    void terminateVote(Long id);
}

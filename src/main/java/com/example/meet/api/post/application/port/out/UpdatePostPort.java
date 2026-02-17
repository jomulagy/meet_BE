package com.example.meet.api.post.application.port.out;

import com.example.meet.api.post.adapter.in.dto.in.UpdatePostRequestDto;

import java.time.LocalDate;

public interface UpdatePostPort {
    long update(UpdatePostRequestDto inDto);

    void terminateVote(Long id);

    void updateMeetDate(Long postId, LocalDate meetDate);
}

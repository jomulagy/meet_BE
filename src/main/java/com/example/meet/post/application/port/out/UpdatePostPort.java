package com.example.meet.post.application.port.out;

import com.example.meet.post.adapter.in.dto.in.UpdatePostRequestDto;

import java.time.LocalDateTime;

public interface UpdatePostPort {
    long update(UpdatePostRequestDto inDto);

    void updateDate(Long id, LocalDateTime dateTime);

    void updatePlace(Long id, String place);
}

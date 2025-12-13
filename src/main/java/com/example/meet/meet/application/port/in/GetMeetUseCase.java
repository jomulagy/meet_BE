package com.example.meet.meet.application.port.in;

import com.example.meet.meet.application.domain.entity.Meet;

public interface GetMeetUseCase {
    Meet get(Long meetId);
}

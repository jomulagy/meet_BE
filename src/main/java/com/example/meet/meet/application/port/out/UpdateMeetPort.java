package com.example.meet.meet.application.port.out;

import java.time.LocalDateTime;

public interface UpdateMeetPort {
    long updateDate(Long meetId, LocalDateTime dateTime);
}

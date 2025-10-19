package com.example.meet.meet.application.port.out;

import com.example.meet.meet.application.domain.entity.Meet;
import java.time.LocalDateTime;
import java.util.List;

public interface GetMeetEndDateBeforePort {
    List<Meet> get(LocalDateTime currentDate);
}

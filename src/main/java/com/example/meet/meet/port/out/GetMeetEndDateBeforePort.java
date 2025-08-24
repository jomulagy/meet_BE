package com.example.meet.meet.port.out;

import com.example.meet.meet.domain.entity.Meet;
import java.time.LocalDateTime;
import java.util.List;

public interface GetMeetEndDateBeforePort {
    List<Meet> get(LocalDateTime currentDate);
}

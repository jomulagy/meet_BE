package com.example.meet.meet.application.port.out;

import com.example.meet.meet.application.domain.entity.Meet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GetMeetPort {
    Optional<Meet> getMeetById(Long meetId);

    List<Meet> getMeetEndDateBeforePort(LocalDateTime currentDate);
}

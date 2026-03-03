package com.example.meet.api.attendance.application.port.out;

import com.example.meet.api.attendance.application.domain.entity.Penalty;

public interface CreatePenaltyPort {
    Penalty save(Penalty penalty);
}

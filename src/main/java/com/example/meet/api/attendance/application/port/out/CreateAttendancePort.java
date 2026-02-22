package com.example.meet.api.attendance.application.port.out;

import com.example.meet.api.attendance.application.domain.entity.Attendance;

public interface CreateAttendancePort {
    Attendance save(Attendance attendance);
}

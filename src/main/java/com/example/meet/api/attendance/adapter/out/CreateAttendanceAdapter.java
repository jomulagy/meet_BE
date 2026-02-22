package com.example.meet.api.attendance.adapter.out;

import com.example.meet.api.attendance.application.domain.entity.Attendance;
import com.example.meet.api.attendance.application.port.out.CreateAttendancePort;
import com.example.meet.infrastructure.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CreateAttendanceAdapter implements CreateAttendancePort {
    private final AttendanceRepository attendanceRepository;

    @Override
    public Attendance save(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }
}

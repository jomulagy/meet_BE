package com.example.meet.api.attendance.application.port.in;

import com.example.meet.api.attendance.adapter.in.dto.in.SaveAttendanceRequestDto;
import com.example.meet.api.attendance.adapter.in.dto.out.SaveAttendanceResponseDto;

public interface SaveAttendanceUseCase {
    SaveAttendanceResponseDto saveAttendance(SaveAttendanceRequestDto requestDto);
}

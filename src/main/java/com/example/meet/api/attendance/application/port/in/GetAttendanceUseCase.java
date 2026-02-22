package com.example.meet.api.attendance.application.port.in;

import com.example.meet.api.attendance.adapter.in.dto.out.GetAttendanceResponseDto;

public interface GetAttendanceUseCase {
    GetAttendanceResponseDto getAttendanceByPostId(Long postId);
}

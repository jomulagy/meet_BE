package com.example.meet.api.attendance.application.port.in;

import com.example.meet.api.attendance.adapter.in.dto.out.AbsencesResponseDto;

public interface GetAbsencesUseCase {
    AbsencesResponseDto getAbsences();
}

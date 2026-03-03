package com.example.meet.api.attendance.application.port.in;

import com.example.meet.api.attendance.adapter.in.dto.in.UpdatePenaltyRequestDto;

public interface UpdatePenaltyUseCase {
    void updatePenaltyStatus(Long memberId, UpdatePenaltyRequestDto requestDto);
}

package com.example.meet.api.attendance.application.service;

import com.example.meet.api.attendance.adapter.in.dto.in.UpdatePenaltyRequestDto;
import com.example.meet.api.attendance.application.domain.entity.Penalty;
import com.example.meet.api.attendance.application.port.in.UpdatePenaltyUseCase;
import com.example.meet.api.attendance.application.port.out.CreatePenaltyPort;
import com.example.meet.api.attendance.application.port.out.GetPenaltyPort;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdatePenaltyService implements UpdatePenaltyUseCase {
    private final GetPenaltyPort getPenaltyPort;
    private final CreatePenaltyPort createPenaltyPort;

    @Override
    public void updatePenaltyStatus(Long memberId, UpdatePenaltyRequestDto requestDto) {
        Penalty latestPenalty = getPenaltyPort.findLatestByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PENALTY_NOT_EXISTS));

        if (requestDto.getPenaltyPaid()) {
            latestPenalty.markPenaltyPaid();
        }

        createPenaltyPort.save(latestPenalty);
    }
}
